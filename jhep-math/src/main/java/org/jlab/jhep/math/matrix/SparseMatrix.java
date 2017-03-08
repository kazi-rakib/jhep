/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.math.matrix;

import org.jlab.hep.math.data.DataAxis;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jlab.hep.math.data.DataDescription;
import org.jlab.hep.math.data.DataVector;
import org.jlab.jhep.math.index.SparseIndex;

/**
 *
 * @author gavalian
 */
public class SparseMatrix {
    
    private SparseIndex index = null;
    private Map<Long,DataVector<Float> > matrixElements = new LinkedHashMap<Long,DataVector<Float>>();
    private List<DataAxis>       matrixAxis = new ArrayList<DataAxis>();
    private Map<String, List<String> > matrixDescription = new LinkedHashMap<String, List<String>>();
    
    private DataDescription     matrixDataDescription = new DataDescription();
    private String name = "";
    
//    private Integer 
    public SparseMatrix(){
        
    }
    
    public SparseMatrix(String[] columns){
        for(int i = 0; i < columns.length; i++){
            this.matrixDataDescription.addDescription(columns[i], columns[i], "float");
        }
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
    
    public final void setName(String __name){ name = __name; }
    public final String getName(){ return name; }
    
    public final void addData( DataVector vec, int... index){

        if(this.hasEntry(index)==true){
            System.out.println("[MATRIX] >>>> error : matrix already has element : " + index.toString());
            return;
        }
        Long key = this.index.getKey(index);
        this.matrixElements.put(key, vec);
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
    
    public final int[] getDimensions(){
        int[] dims = new int[this.matrixAxis.size()];
        for(int i = 0; i < dims.length; i++) dims[i] = matrixAxis.get(i).getBins();
        return dims;
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
    
    public static void main(String[] args){
        SparseMatrix  matrix = new SparseMatrix();
        DataAxis  axisX = new DataAxis("X",25,0.0,1.0);
        DataAxis  axisY = new DataAxis("Y",35,0.0,2.5);
        matrix.initAxis(axisX,axisY);
        for(int i = 0; i < 10; i++){
            DataVector<Float>  vec = new DataVector<Float>(new Float[]{ (float) 0.1, (float) 0.4});
            matrix.addData(vec, i+1,1);
        }
        
        //matrix.printContent();
        //matrix.initMatrix(25,35,120);
    }
}
