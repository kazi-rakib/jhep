/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

/**
 *
 * @author gavalian
 */
public interface DataEvent {
    
    public int getEntries();
    
    public int       getSize(  int hash );    
    public int        getInt(  int hash, int index );
    public short    getShort(  int hash, int index );
    public float    getFloat(  int hash, int index );
    public String  getString(  int hash, int index );
    public double  getDouble(  int hash, int index );    
    public int       getHash(  int... indices);
    
}
