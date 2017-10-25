/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataVector<T extends Number> {
    
    private List<T> dataVector = new ArrayList<T>();
    private Boolean    ordered = true;
    private Boolean  fixedSize = false;
    private T          maximum = null;
    private T          minimum = null;
    
    public DataVector(){
        
    }
    /**
     * creates a data vector with given size and fills all elements 
     * with given value.
     * @param size
     * @param value 
     */
    public DataVector(int size, T value){
        for(int i = 0; i < size; i++){
            dataVector.add(value);
        }
        this.minimum = value;
        this.maximum = value;
        this.ordered = false;
        this.fixedSize = true;
    }
    /**
     * creates a fixed size Data Vector and fills with given values
     * @param values values to fill data vector with.
     */
    public DataVector(T... values){
        for(int i = 0; i < values.length; i++){
            dataVector.add(values[i]);
        }
        this.minimum = values[0];
        this.maximum = values[0];
        this.ordered = false;
    }
    
    public T valueOf(int index){
        return this.dataVector.get(index);
    }
    
    public void setValue(int index, T value){
        this.dataVector.set(index, value);
    }
    
    public int getSize(){
        return this.dataVector.size();
    }
        
    public final Boolean isFixedSize(){return this.fixedSize;}
    public final void setFixedSize(boolean __fixed){
        this.fixedSize = __fixed;
    }
    /*
    public void add(DataVector vector){
        if(vector.getSize()!=this.getSize()){
            System.out.println("[DataVector] error adding vectors with different sizes");
            return;
        }
        for(int i = 0; i < vector.getSize(); i++){
            T value = this.dataVector.get(i);
            this.dataVector.set(i, value + vector.valueOf(i));
        }

    }*/
    public final void show(){
        System.out.print("VEC: ");
        for(T v : dataVector){
            System.out.print(v + " ");
        }
        System.out.println();
    }
    
    public int getRandomBin(){
        double rndm = Math.random();
        int i = 0;
        for(T item : dataVector){
            if(rndm>=item.doubleValue()) return i;
            i++;
        }
        return -1;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this.getSize(); i++){
            str.append(String.format("%12.5f ", valueOf(i)));
        }
        return str.toString();
    }
}
