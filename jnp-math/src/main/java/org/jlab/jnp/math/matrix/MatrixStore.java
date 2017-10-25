/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jlab.jnp.math.data.DataVector;

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
    
    public void readMatrix(String name, String filename){
        SparseMatrix matrix = SparseMatrixReader.readHipoMatrix(filename);
        this.matrixStore.put(name, matrix);
    }
    
    public void readSparseMatrix(String name, String filename){
        SparseMatrix matrix = SparseMatrixReader.readSparseMatrix(filename);
        this.matrixStore.put(name, matrix);
    }
    
    public void readMatrix(String filename){
        SparseMatrix matrix = SparseMatrixReader.readHipoMatrix(filename);
        this.matrixStore.put(matrix.getName(), matrix);
    }
    
    public static void main(String[] args){
        
        MatrixStore store = new MatrixStore();
        store.readMatrix("FUUT", "/Users/gavalian/Work/Software/project-1a.0.0/Distribution/jhep/jhep-math/FUUT_f.hipo");
        
        store.getMatrix("FUUT").show();
        
        for(double a = 0.1; a < 2.0; a+=0.05){
            double value = store.getMatrix("FUUT").evaluate(0, 0.5,1.5,0.5,a);
            System.out.println(" a = " + a + "  value = " + value);
        }
        
        DataVector  vec = store.getMatrix("FUUT").getAxisVector(2);
        DataVector data = store.getMatrix("FUUT").getProjection(2, 0);
        
        vec.show();
        data.show();
        
        
        store.getMatrix("FUUT").printContent();
    }
}
