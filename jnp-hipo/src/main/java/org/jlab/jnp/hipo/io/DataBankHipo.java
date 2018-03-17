/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import java.util.List;
import org.jlab.jnp.hipo.base.DataBank;
import org.jlab.jnp.hipo.base.DataDescriptor;
import org.jlab.jnp.hipo.schema.Schema;

/**
 *
 * @author gavalian
 */
public class DataBankHipo implements DataBank {

    protected Schema         bankSchema = null;
    protected DataEventHipo  dataEvent = null;
    
    protected void setSchema(Schema schema){
        bankSchema = schema;
    }
    
    protected void setDataEvent(DataEventHipo event){
        dataEvent = event;
    }
    
    @Override
    public int getSize(String name) {
        int hash = bankSchema.getEntry(name).getHash();
        return dataEvent.getSize(hash);
    }

    @Override
    public byte getByte(String name, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short getShort(String name, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getInt(String name, int index) {
        return dataEvent.getInt(bankSchema.getEntry(name).getHash(), index);
    }

    @Override
    public long getLong(String name, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getFloat(String name, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getDouble(String name, int index) {
        return dataEvent.getDouble(bankSchema.getEntry(name).getHash(), index);
    }
        
    public void show(){
        System.out.println("------------------------+---------------------------+");
        System.out.println(String.format(">>>> GROUP (group=%6d) (name=%s):", 
                this.bankSchema.getGroup(),this.bankSchema.getName()));
        System.out.println("------------------------+---------------------------+");
        List<String> entries = this.bankSchema.schemaEntryList();
        for(String entry : entries){
            int hash = bankSchema.getEntry(entry).getHash();
            System.out.println(String.format("%12s (%4d): %d", entry,dataEvent.getSize(hash),
                    hash));
        }
    }

    @Override
    public void setDescriptor(DataDescriptor desc) {
        this.bankSchema = (Schema) desc;
    }

    @Override
    public DataDescriptor getDescriptor() {
        return this.bankSchema;
    }

    @Override
    public List<String> getKeys() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte getByte(int id, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short getShort(int id, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getInt(int id, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getLong(int id, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getFloat(int id, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getDouble(int id, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
