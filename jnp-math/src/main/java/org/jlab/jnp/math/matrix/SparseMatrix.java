/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.matrix;

import org.jlab.jnp.math.data.DataAxis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jlab.jnp.math.data.DataDescription;
import org.jlab.jnp.math.data.DataVector;
import org.jlab.jnp.math.index.SparseIndex;

/**
 *
 * @author gavalian
 */
public class SparseMatrix {
    
    private SparseIndex index = null;
    private Map<Long,DataVector<Float> >  matrixElements = new LinkedHashMap<Long,DataVector<Float>>();
    private List<DataAxis>                    matrixAxis = new ArrayList<DataAxis>();
    private Map<String, List<String> > matrixDescription = new LinkedHashMap<String, List<String>>();
    
    private DataDescription     matrixDataDescription = new DataDescription();
    private String name = "";
    private int    vectorSize = 0;
    private String jsonHeader = "";
//    private Integer 
    public SparseMatrix(){
        
    }
    
    public SparseMatrix(String[] columns){
        for(int i = 0; i < columns.length; i++){
            this.matrixDataDescription.addDescription(columns[i], columns[i], "float");
        }
        vectorSize = columns.length;
    }
    
    public static SparseMatrix build(String[] columns, DataAxis... axis){
        SparseMatrix matrix = new SparseMatrix(columns);
        matrix.initAxis(axis);
        return matrix;
    }
    
    public String getAxisNames(){
        StringBuilder str = new StringBuilder();
        for(DataAxis axis : this.matrixAxis){
            str.append(axis.getName()).append(":");
        }        
        return str.toString();
    }
    
    public List<DataAxis>  getMatrixAxis(){
        return this.matrixAxis;
    }
    
    public int[] getAxisBins(){
        int[] bins = new int[matrixAxis.size()];
        for(int i = 0; i < bins.length; i++) bins[i] = matrixAxis.get(i).getBins();
        return bins;
    }
    public String[] getVectorList(){
        String[] list = new String[this.matrixDataDescription.getSize()];
        for(int i = 0; i < list.length; i++){
            list[i] = this.matrixDataDescription.getDescription(i).getName();
        }
        return list;
    }
    public String getVectorNames(){
        int size = this.matrixDataDescription.getSize();
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < size; i++){
            str.append(matrixDataDescription.getDescription(i).getName()).append(":");
        }
        return str.toString();
    }
    
    public double[] getAxisMins(){
        double[] mins = new double[matrixAxis.size()];
        for(int i = 0; i < mins.length; i++) mins[i] = matrixAxis.get(i).getMin();
        return mins;
    }
    
    public double[] getAxisMaxs(){
        double[] maxs = new double[matrixAxis.size()];
        for(int i = 0; i < maxs.length; i++) maxs[i] = matrixAxis.get(i).getMax();
        return maxs;
    }
    
    public String getAxisTypes(){
        StringBuilder str = new StringBuilder();
        for(DataAxis axis : this.matrixAxis){
            if(axis.isLog()==true){
                str.append("log").append(":");
            } else {
                str.append("lin").append(":");
            }
        }        
        return str.toString();
    }
    
    public final void initAxis(DataAxis... axis){
        this.matrixAxis.clear();
        this.matrixElements.clear();
        for(DataAxis item : axis){
            this.matrixAxis.add(item);
        }
        int[] dims = this.getDimensions();
        this.initMatrix(dims);
    }
    
    public final void initAxis(List<DataAxis> axis){
        this.matrixAxis.clear();
        this.matrixElements.clear();
        for(DataAxis item : axis){
            this.matrixAxis.add(item);
        }
        int[] dims = this.getDimensions();
        this.initMatrix(dims);
    }
    
    public final void setHeader(String header){jsonHeader = header;}
    public final String getHeader(){ return jsonHeader;}
    
    public final int  getRank(){return this.index.getRank();}
    public final int  getVectorSize(){ return vectorSize;}
    
    public final int  getEntries(){ return this.matrixElements.size();}
    
    public final void setName(String __name){ name = __name; }
    public final String getName(){ return name; }
    
    public double evaluate(int index, double... axis){
        int[] bins = new int[axis.length];
        for(int i = 0; i < bins.length; i++){
            bins[i] = matrixAxis.get(i).findBin(axis[i]);
        }
        return  this.getVector(bins).valueOf(index).doubleValue();
    }
    
    public final void addData(long index, DataVector vec){
        this.matrixElements.put(index, vec);
    }
    
    public final void addData( DataVector vec, int... index){
        if(this.matrixElements.isEmpty()==true){
            this.vectorSize = vec.getSize();
        }
        
        if(this.hasEntry(index)==true){
            System.out.println("[MATRIX] >>>> error : matrix already has element : " + index.toString());
            return;
        }
        Long key = this.index.getKey(index);
        this.matrixElements.put(key, vec);
    }
    
    public DataVector getAxisVector(int axis){
        int nbins = this.matrixAxis.get(axis).getBins();
        Double[] binCenters = new Double[nbins];

        for(int i = 0; i < this.matrixAxis.get(axis).getBins();i++){            
            binCenters[i] = this.matrixAxis.get(axis).binCenter(i);
        }
        return new DataVector(binCenters);
    }
    
    public DataVector getProjection(MatrixSelection selector){
        int nbins = this.matrixAxis.get(selector.getBin()).getBins();
        int[] bin = new int[getRank()];
        Double[] binContent = new Double[nbins];
        for(int i = 0; i < binContent.length; i++) binContent[i] = 0.0;
        int axis = selector.getBin();
        int data_index = selector.getColumn();
        System.out.println("-------------> plotting the column " + data_index);
        for(Map.Entry<Long,DataVector<Float>> entry : this.matrixElements.entrySet()){
            this.index.getIndex(entry.getKey(), bin);
            int idx = bin[axis];
            double value = entry.getValue().valueOf(data_index);
            if(selector.isValid(bin)==true){
                binContent[idx] = binContent[idx] + value;
            }
        }
        return (new DataVector<Double>(binContent));
    }
    
    public DataVector  getProjection(int axis, int data_index){
        int nbins = this.matrixAxis.get(axis).getBins();
        Double[] binContent = new Double[nbins];
        int[]    bin        = new int[getRank()];
        for(int i = 0; i < binContent.length; i++) binContent[i] = 0.0;
        for(Map.Entry<Long,DataVector<Float>> entry : this.matrixElements.entrySet()){
            this.index.getIndex(entry.getKey(), bin);
            int idx = bin[axis];
            double value = entry.getValue().valueOf(data_index);
            binContent[idx] = binContent[idx] + value;
        }
        return (new DataVector<Double>(binContent));
    }
    /**
     * prints the content of the MAP, all elements.
     */
    public void printContent(){
        Set<Long> keySet = this.matrixElements.keySet();
        for(Long key : keySet){
            System.out.println(String.format("%14s : %s", this.index.toIndexString(key), this.matrixElements.get(key).toString()));
        }
    }
    
    public String contentString(){
        StringBuilder str = new StringBuilder();
        Set<Long> keySet = this.matrixElements.keySet();
        int[] bins = new int[index.getRank()];
        float[] centers = new float[index.getRank()];
        
        for(Long key : keySet){
            index.getIndex(key, bins);
            for(int i = 0; i < centers.length; i++){
                centers[i] = (float) matrixAxis.get(i).binCenter(bins[i]);
                str.append(String.format(" %e", centers[i]));
            }
            DataVector<Float> data = this.matrixElements.get(key);
            for(int i = 0; i < data.getSize(); i++){
                str.append(String.format(" %e", data.valueOf(i)));
            }
            str.append("\n");
            //System.out.println(String.format("%14s : %s", this.index.toIndexString(key), this.matrixElements.get(key).toString()));
        }
        return str.toString();
    }
    
    public MatrixSelection getSelector(){
        int[] bins = index.getBinsPerAxis();
        MatrixSelection selector = new MatrixSelection(bins);
        int ncols = matrixDataDescription.getSize();
        String[] columns = new String[ncols];
        for(int i = 0; i < ncols; i++){
            columns[i] = this.matrixDataDescription.getDescription(i).getName();
        }
        selector.setColumns(Arrays.asList(columns));
        selector.reset();
        return selector;
    }
    
    public void show(){
       System.out.printf("%15s : rank=%3d entries=%6d\n", getName(),getRank(),getEntries());
    }
    
    public final int[] getDimensions(){
        int[] dims = new int[this.matrixAxis.size()];
        for(int i = 0; i < dims.length; i++) dims[i] = matrixAxis.get(i).getBins();
        return dims;
    }
    
    public SparseIndex getSparseIndex(){
        return index;
    }
    /**
     * initializes matrix with given dimensions.
     * @param dimensions number of bins across axis
     */
    public final void initMatrix(int... dimensions){
        index = new SparseIndex(dimensions);
        System.out.println("[INFO] >> matrix initialized : " + index.toString());
    }
    /**
     * returns true if the matrix has entry with given bins, false otherwise
     * @param bins
     * @return 
     */
    public final boolean hasEntry(int... bins){
        Long key = this.index.getKey(bins);
        return this.matrixElements.containsKey(key);
    }
    
    public final DataVector<Float> getEntry(int... bins){
        Long key = this.index.getKey(bins);
        return this.matrixElements.get(key);
    }
    /**
     * adds description for the matrix.
     * @param key description string
     * @param value description value
     */
    public final void addDescription(String key, String value){
        if(this.matrixDescription.containsKey(key)==false){
            List<String>  valueList = new ArrayList<String>();
            valueList.add(value);
            this.matrixDescription.put(key, valueList);
        } else {
            matrixDescription.get(key).add(value);
        }
    }
    
    public void fill(int order, Map<String,Double> values, double weight){
        
        int[] bins = new int[this.getRank()];
        boolean flag = true;
        for(int i = 0; i < this.matrixAxis.size(); i++){
            String name = this.matrixAxis.get(i).getName();
            if(values.containsKey(name)==false){
                System.out.println("[] fill : map does not have variable : " + name);
                return;
            } else {
                bins[i] = this.matrixAxis.get(i).findBin(values.get(name));
                if(bins[i]<0) flag = false;
            }
        }
        
        /*if(flag==false){
            System.out.println("----> fill command failed");
        }*/
        
        long key = this.index.getKey(bins);
        
        if(this.matrixElements.containsKey(key)==false){
            this.matrixElements.put(key, new DataVector<Float>(this.matrixDataDescription.getSize(),0.0f));
        }
        
        DataVector<Float> vector = this.matrixElements.get(key);
        double ov = vector.valueOf(order);
        ov += weight;
        vector.setValue(order, (float) ov);
    }
    /**
     * returns cell size for the given bin in the matrix.
     * @param bins bins in each direction
     * @return cell width
     */
    public final double getBinSize(int... bins){
        double width = 1.0;
        for(int i = 0; i < bins.length; i++){
            double w = this.matrixAxis.get(i).getBinWidth(bins[i]);
            width *= w;
        }
        return width;
    }
    
    public final DataVector getVector(int... bins){
        if(this.hasEntry(bins)==false) {
            return null;
        }
        Long key = this.index.getKey(bins);
        return this.matrixElements.get(key);
    }
    
    public final double integral(){
        return 1.0;
    }
    
    
    public Map<Long,DataVector<Float>> getMatrixMap(){ return this.matrixElements;}
    
    public static void main(String[] args){
        
        SparseMatrix  matrix = new SparseMatrix(new String[]{"cross","acceptance"});
        DataAxis  axisX = new DataAxis("a",25,0.0,1.0);
        DataAxis  axisY = new DataAxis("b",35,0.0,2.5);
        matrix.initAxis(axisX,axisY);
        /*for(int i = 0; i < 10; i++){
            DataVector<Float>  vec = new DataVector<Float>(new Float[]{ (float) 0.1, (float) 0.4});
            matrix.addData(vec, i+1,1);
        }*/
        
        Map<String,Double>  items = new HashMap<String,Double>();
        for(int i = 0; i < 3200; i++){
            double a = Math.random();
            double b = Math.random();
            double w = Math.random();
            
            items.clear();
            items.put("a", a); items.put("b", b);
            matrix.fill(0, items, 1.0);
            matrix.fill(1, items, w);
        }
        matrix.show();
        matrix.printContent();
//System.out.println(matrix.toString());
        
        //matrix.printContent();
        //matrix.initMatrix(25,35,120);
    }
}
