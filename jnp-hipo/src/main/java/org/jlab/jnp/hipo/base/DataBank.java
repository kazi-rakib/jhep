/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

import java.util.List;

/**
 * Generic data bank interface. Implements all data
 * type that can be stored and returned by data bank.
 * @author gavalian
 */
public interface DataBank {
    
    public List<String>  getKeys();
    /**
     * Returns the data size in the leaf with given name.
     * @param name name of the leaf
     * @return data array size
     */
    
    int    getSize(   String name);
    
    byte   getByte(   int id, int index);
    short  getShort(  int id, int index);
    int    getInt(    int id, int index);
    long   getLong(   int id, int index);
    float  getFloat(  int id, int index);
    double getDouble( int id, int index);
    /**
     * returns Byte stored at index-th position of leaf "name".
     * @param name name of the leaf
     * @param index position in the array
     * @return 
     */
    
    byte   getByte(   String name, int index);    
    short  getShort(  String name, int index);
    int    getInt(    String name, int index);
    long   getLong(   String name, int index);
    float  getFloat(  String name, int index);
    double getDouble( String name, int index);
    
    public void setDescriptor(DataDescriptor desc);
    
    public DataDescriptor getDescriptor();
}
