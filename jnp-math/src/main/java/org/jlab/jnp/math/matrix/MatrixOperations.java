/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.math.data.DataAxis;
import org.jlab.jnp.math.data.DataVector;
import org.jlab.jnp.math.index.SparseIndex;

/**
 *
 * @author gavalian
 */
public class MatrixOperations {
    
    public static int[] removeBin(int[] bins, int b){
        int[] nbins = new int[bins.length-1];
        int index = 0;
        for(int i = 0; i < bins.length;i++){
            if(i!=b){
                nbins[index] = bins[i];
                index++;
            }
        }
        return nbins;
    }
    
    public static SparseMatrix integrate(SparseMatrix matrix, int dims){
        
        List<DataAxis> axisList = new ArrayList<DataAxis>();
        
        List<DataAxis>  matrixAxis = matrix.getMatrixAxis();
        for(int i = 0; i < matrixAxis.size(); i++){
            boolean addAxis = true;
            //for(int d : dims) if(d==i) addAxis = false;
            if(i!=dims){
                axisList.add(DataAxis.from(matrixAxis.get(i)));
            }
        }
        
        SparseMatrix matrixInt = new SparseMatrix(matrix.getVectorList());
        matrixInt.initAxis(axisList);
        
        Map<Long,DataVector<Float> > map = matrix.getMatrixMap();
        
        SparseIndex indexer = matrix.getSparseIndex();
        int[] bins = new int[indexer.getRank()];
        for(Map.Entry<Long,DataVector<Float>> entry : map.entrySet()){
            Long key = entry.getKey();
            indexer.getIndex(key, bins);
            int[] reducedBins = MatrixOperations.removeBin(bins, dims);
            //System.out.println(MatrixOperations.binsString(bins) + " --> " + MatrixOperations.binsString(reducedBins));
            if(matrixInt.hasEntry(reducedBins)==false){
                matrixInt.addData(new DataVector<Float>(matrixInt.getVectorSize(),0.0f), reducedBins);
            }
            
            DataVector<Float>  newVec = matrixInt.getEntry(reducedBins);
            DataVector<Float>  vector = matrix.getEntry(bins);
            int ncolumns = vector.getSize();
            for(int c = 0; c < ncolumns; c++){
                float value = newVec.valueOf(c);
                newVec.setValue(c, value + vector.valueOf(c));
            }
        }
        
        matrixInt.show();
        return matrixInt;
    }
    
    public static String binsString(int[] bins){
        StringBuilder str = new StringBuilder();
        str.append("{");
        for(int i = 0; i < bins.length; i++) str.append(String.format(" %d,", bins[i]));              
        str.append("}");
        return str.toString();
    }
}
