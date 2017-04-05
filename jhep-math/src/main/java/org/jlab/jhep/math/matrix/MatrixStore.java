/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.math.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gavalian
 */
public class MatrixStore {
    private Map<String,SparseMatrix>  matrixStore = new HashMap<String,SparseMatrix>();
    
    public MatrixStore(){
        
    }
    
    public Set<String>   getSet(){ return matrixStore.keySet();}
    public SparseMatrix  getMatrix(String name){ return matrixStore.get(name);}

    public void show(){
        for(Map.Entry<String,SparseMatrix> entry : matrixStore.entrySet()){
            entry.getValue().show();
        }
    }
    
    public void readMatrix(String filename){
        SparseMatrix matrix = SparseMatrixReader.readHipoMatrix(filename);
        this.matrixStore.put(matrix.getName(), matrix);
    }
}
