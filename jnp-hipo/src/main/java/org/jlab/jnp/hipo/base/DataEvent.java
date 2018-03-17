/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

import java.nio.ByteBuffer;

/**
 *
 * @author gavalian
 */
public interface DataEvent {

    /**
     * Returns instance of a data bank associated with name
     * @param name name of the bank
     * @param bank DataBank structure
     * @return true if the bank existed, false otherwise
     */
    public boolean getBank(String name, DataBank bank);
    
    /**
     * returns the number of entries in the event.
     * Typically number of nodes.
     * @return 
     */
    public int getEntries();
    /**
     * returns size of given node with given hash.
     * @param hash hash code for the node.
     * @return size of the data (not the byte count)
     */
    public int        getSize(  int hash );
    /**
     * returns the data type for given hash code.
     * @param hash hash code of the node
     * @return data type
     */
    public int        getType( int hash);
    /**
     * returns relative position of the node in the event.
     * @param hash hash code for the leaf
     * @return relative offset
     */
    public int getPosition(int hash);
    /**
     * returns an integer value of i-th index from 
     * the node. User must take care of checking the type.
     * @param hash
     * @param index
     * @return 
     */
    public int         getInt(  int hash, int index );
    public short     getShort(  int hash, int index );
    public float     getFloat(  int hash, int index );
    public String   getString(  int hash, int index );
    public double   getDouble(  int hash, int index );
    /**
     * returns integer for integer type variable located at position
     * offset.
     * @param position offset of the node
     * @param index array element
     * @return value
     */
    public int getIntAt(int position, int index);
    /**
     * return floating point number for the node at position offset
     * @param potistion offset of the node
     * @param index array element
     * @return value
     */
    public double getDoubleAt(int potistion, int index);
    /**
     * constructs a hash code for given indices each 
     * implementation can have their own hash code 
     * for nodes.
     * @param indices indices of the node
     * @return hash code
     */
    public int        getHash(  int... indices);
    /**
     * Returns a Byte buffer of serialized event content
     * @return byte buffer with event content
     */
    public ByteBuffer getEventBuffer();
    /**
     * returns event size in bytes
     * @return byte size of the event
     */
    public int      getSize();
    /**
     * sets the size for the event.
     * @param size expected event size
     * @return true if successful, false otherwise
     */
    public boolean  setSize(int size);
    /**
     * After reading the event rehash function runs indexing 
     * of the event to find all components
     * @return 
     */
    public int rehash();
    
    public void setDictionary(DataDictionary dict);
    
    public DataDictionary getDictionary();
    /**
     * returns type of the data event. depends on implementation.
     * @return data event type
     */
    public int getType();
}
