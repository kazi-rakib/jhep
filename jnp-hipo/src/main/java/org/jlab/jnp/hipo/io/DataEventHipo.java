/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jlab.jnp.hipo.base.DataEvent;
import org.jlab.jnp.hipo.data.HipoNodeType;
import org.jlab.jnp.utils.data.TextTable;
import org.jlab.jnp.utils.maps.IntIntMap;

/**
 *
 * @author gavalian
 */
public class DataEventHipo implements DataEvent {
    
    protected final int           EVENT_HEADER_LENGTH = 16;
    protected final int            NODE_HEADER_LENGTH = 8;
    protected final int    EVENT_LENGTH_WORD_POSITION = 4;
    
    /**
     * Buffer containing the event information.
     */
    protected ByteBuffer  eventBuffer = null;
    /**
     * Leafs Map contains reference to leaf nodes in the event.
     * The key is a hash constructed as (group shift 16|item)
     */
    private DataEventLeafs leafsMap = new DataEventLeafs();
    /**
     * Creates a new DataEventHipo with capacity of
     * 4 bytes. When reading in data it will expand by 
     * requesting soft resize()
     */
    public DataEventHipo(){
        byte[] buffer = new byte[4];
        eventBuffer = ByteBuffer.wrap(buffer);
        eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    /**
     * Soft resize method. The buffer is only being resized if the requested
     * size is larger than current buffer size.
     * @param requestedSize 
     */
    protected void resize(int requestedSize){
        if(eventBuffer.capacity()<requestedSize){
            //System.out.println("*** resizing event *** size = " + (requestedSize+4));
            byte[] buffer = new byte[requestedSize+4];
            eventBuffer = ByteBuffer.wrap(buffer);
            eventBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
    }
    
    
    public void init(ByteBuffer buffer, int startPosition, int length){
        resize(length);
        System.arraycopy(buffer.array(), startPosition, 
                eventBuffer.array(), 0, length);
        //System.out.println(" LENGTH = " + length);
        eventBuffer.putInt(EVENT_LENGTH_WORD_POSITION, length);
        updateIndex();
    }
    
    protected void updateIndex(){
        int   capacity = eventBuffer.getInt(EVENT_LENGTH_WORD_POSITION);
        int   position = EVENT_HEADER_LENGTH;
        //System.out.println("** start update index ** position = " + position
        //+ " capacity = " + capacity);
        this.leafsMap.reset();
        int counter = 0;
        try {
            while((position+NODE_HEADER_LENGTH)<=capacity){
                
                short group = eventBuffer.getShort( position    );
                byte  item  = eventBuffer.get(      position + 2);
                byte  type  = eventBuffer.get(      position + 3);
                int   size  = eventBuffer.getInt(   position + 4);
                int    key  = getHash(group,item);
                counter++;
                //if(counter<6)
                //if(group==331)
                leafsMap.addLeaf( key , position);
                position += NODE_HEADER_LENGTH + size;
            }
        } catch (Exception e){
            //System.out.println("*** error parsing event ****");
        }
        
    }
    /**
     * returns the number of items in the event. For consistency checks.
     * @return number of nodes
     */
    @Override
    public int getEntries() {
        return leafsMap.getCount();
    }
    /**
     * Returns the type of the Node by given hash code.
     * @param hash
     * @return 
     */
    public HipoNodeType getType(int hash){
        int position = leafsMap.getPosition(hash);
        if(position<0) return HipoNodeType.UNDEFINED;
        int type = (int) eventBuffer.get(position+3);
        return HipoNodeType.getType(type);
    }
    
    protected HipoNodeType getNodeTypeByPosition(int position){
        int type = (int) eventBuffer.get(position+3);
        return HipoNodeType.getType(type);
    }
    
    @Override
    public int getSize(int hash) {
        int position = leafsMap.getPosition(hash);
        if(position<0) return 0;
        HipoNodeType   type = this.getNodeTypeByPosition(position);
        int    bufferLength = eventBuffer.getInt(position+4);
        int           ndata = bufferLength/type.getSize();
        return ndata;        
    }
    
    @Override
    public int getInt(int hash, int index) {
        int position = leafsMap.getPosition(hash);
        int type = (int) eventBuffer.get(position+3);
        //if()
        switch(type){
            case 1: return (int) eventBuffer.get(position+this.NODE_HEADER_LENGTH+index);
            case 2: return (int) eventBuffer.getShort(position+this.NODE_HEADER_LENGTH+index*2);
            case 3: return eventBuffer.getInt(position+this.NODE_HEADER_LENGTH+index*4);
            default: System.out.println("*** error *** getInt() - the node is not and integer. hash = " + hash);
        }
        return 0;
    }

    @Override
    public short getShort(int hash, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getFloat(int hash, int index) {
        int position = leafsMap.getPosition(hash);
        int type = (int) eventBuffer.get(position+3);
        //if()
        switch(type){
            case 4: return  eventBuffer.getFloat(position+this.NODE_HEADER_LENGTH+index*4);
            default: System.out.println("*** error *** getFloat() - the node is not a float. hash = " + hash);
        }
        return 0;
    }

    @Override
    public String getString(int hash, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getDouble(int hash, int index) {
        int position = leafsMap.getPosition(hash);
        int type = (int) eventBuffer.get(position+3);
        //if()
        switch(type){
            case 4: return (double) eventBuffer.getFloat(position+this.NODE_HEADER_LENGTH+index*4);
            case 5: return eventBuffer.getDouble(position+this.NODE_HEADER_LENGTH+index*8);
            default: System.out.println("*** error *** getFloat() - the node is not a float. hash = " + hash);
        }
        return 0;
    }

    @Override
    public int getHash(int... indices) {
        int hash_int = 0;
        if(indices.length>=2){
            int group = indices[0];
            int node  = indices[1];
            hash_int = (group<<16)|(node);
        }
        return hash_int;
    }
    
    
    public void show(){
        int[]  keys = leafsMap.getKeys();
        
        TextTable table = new TextTable("group:item:type:length:words:position","8:8:12:8:8:12");
        for(int key : keys){
            Integer  group = key >> 16;
            Integer   item = key&(0x000000FF);
            Integer length = getSize(key);
            Integer    pos = leafsMap.getPosition(key);
            HipoNodeType type = getType(key);
            Integer   data = getType(key).getSize()*length;
            String   typeString = String.format("%s(%d)", type.getName(),type.getType());
            table.addData(new String[]{group.toString(),item.toString(),
                    typeString,length.toString(), data.toString() ,pos.toString()});
        }
        System.out.println(table.toString());
    }
    /**
     * Class for keeping track of the leafs in the event. It uses experimental
     * IntIntMap which stores values in the native integer, it works much faster
     * than JDK Map. May change the implementation if I find more efficient map.
     */
    public static class DataEventLeafs {
        private IntIntMap  leafsMap = null;
        private int[]      results  = null;
        
        public DataEventLeafs(){
            leafsMap = new IntIntMap(800,0.75F);
            results  = new int[2];
        }
        
        
        public int getCount(){
            return leafsMap.size();
        }
        
        public void addLeaf(int hash, int position){
            leafsMap.put(hash, position);
        }
        
        public boolean hasLeaf(int hash){
            return leafsMap.contains(hash);
        }
        
        public int getPosition(int hash){
            results[0] = -1;
            leafsMap.get(hash, results);
            return results[0];
        }
        
        public void reset(){
            leafsMap = new IntIntMap(100,0.75F);
            //leafsMap = new IntIntMap();
            /*int[] keys = leafsMap.keys();
            for(int key: keys){
                //leafsMap.put(key,-1);
                leafsMap.remove(key);
            }*/
        }
        
        public int[] getKeys(){
            return leafsMap.keys();
        }
    };
}
