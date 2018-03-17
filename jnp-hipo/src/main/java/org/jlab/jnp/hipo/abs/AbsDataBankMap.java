/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.abs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import org.jlab.jnp.hipo.base.DataBank;
import org.jlab.jnp.hipo.base.DataDescriptor;
import org.jlab.jnp.hipo.data.HipoNodeType;
import org.jlab.jnp.hipo.schema.Schema;

/**
 *
 * @author gavalian
 */
public class AbsDataBankMap implements DataBank {
    
    protected Schema      mapSchema = null;
    protected ByteBuffer  mapBuffer = null;
    private   int         mapEntries = 0;
    
    public AbsDataBankMap(String[] rows){
        mapSchema = new Schema();        
        mapSchema.addEntry("STATUS", 0, HipoNodeType.INT);
        for(int i = 0; i < rows.length; i++){
            mapSchema.addEntry(rows[i], i+1, HipoNodeType.DOUBLE);
        }
        mapEntries = rows.length+1;
    }
    
    public AbsDataBankMap(String[] rows, int initialCapacity){
        mapSchema = new Schema();        
        mapSchema.addEntry("STATUS", 1, HipoNodeType.INT);
        for(int i = 0; i < rows.length; i++){
            mapSchema.addEntry(rows[i], i+2, HipoNodeType.DOUBLE);
        }
        int unitSize = rows.length*8 + rows.length*4;
        byte[] buffer = new byte[4+unitSize*initialCapacity];
        mapBuffer = ByteBuffer.wrap(buffer);
        mapBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mapBuffer.putInt(0, 0);
        mapEntries = rows.length+1;
    }
    
    @Override
    public List<String> getKeys() {
       return mapSchema.schemaEntryList();
    }

    @Override
    public int getSize(String name) {
        return mapBuffer.getInt(0);
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

    public void setStatus(int index, int status){
        int idoffset = (index-1)*8 + 4;
    }
    
    public void setSize(int size){
        this.mapBuffer.putInt(0, size);
    }
    
    private int getDataOffset(int id, int index){
        int idoffset = (index-1)*8 + 4;
        int entryLength = mapEntries*8 - 4;
        int offset   = 4 + id*entryLength + idoffset;
        return offset;
    }
    
    public void setDouble(int id, int index, double value){
        int offset = getDataOffset(id,index);
        this.mapBuffer.putDouble(value);
    }
    
    @Override
    public double getDouble(int id, int index) {
        int offset   = getDataOffset(id,index);
        System.out.println(" id = " + id + " index = " + index + "  offset = " 
                + offset + " size = " + this.mapBuffer.array().length);
        return mapBuffer.getDouble(offset);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDescriptor(DataDescriptor desc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DataDescriptor getDescriptor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void show(){
        System.out.println(this.toString());
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        int entries = this.mapBuffer.getInt(0);
        
        for(int i = 0; i < entries; i++){
            str.append(String.format("%2d : ", 1));
            for(int r = 0; r < this.mapEntries; r++){
                str.append(String.format("%8.5f", getDouble(r,i)));
            }
            str.append("\n");
        }
        return str.toString();
    }
    
    public static void main(String[] args){
        String[] rows = new String[]{"a","b","c","d"};
        AbsDataBankMap map = new AbsDataBankMap(rows,2);
        map.setSize(2);
        map.show();
    }
}
