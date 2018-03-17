/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.coda.hipo.HipoException;
import org.jlab.coda.hipo.Reader;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.hipo.schema.Schema;
import org.jlab.jnp.hipo.schema.SchemaFactory;

/**
 *
 * @author gavalian
 */
public class AbsDataRecordQueue {
    
    protected ByteBuffer   queueBuffer = null;
    private   Semaphore    readsem     = null;
    
    private int       position = 1;
    private int        entries = 0;
    
    private int       currentChunkCounter = 0;
    private int       chunkSize           = 0;
    private int       numberOfChunks      = 0;
    private int       nrecords            = 0;
    private int       currentRecord       = 0;
    Reader            reader              = null;
    SchemaFactory    factory = new SchemaFactory();
    
    public AbsDataRecordQueue(String filename, int record){
        try {
            reader = new Reader(filename, true);
            //reader.open(filename);
            nrecords = reader.getRecordCount();
            reader.readRecord(record);
            currentRecord = record;
            position = 1;
            entries  = reader.getCurrentRecordStream().getEntries()-1;
            System.out.println(" # entries = " + entries);
            readsem = new Semaphore(1, true);
        } catch (HipoException ex) {
            Logger.getLogger(AbsDataRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        factory.addSchema(new Schema("mc::event" , 32111, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:mass/F:parent/B:status/B"));
    }        
    
    
    public synchronized int pop(List<DataEventHipo> event){
        
        int    nread = event.size();
        int icounter = 0;
        
        for(int i = 0; i < nread; i++){
            if(position>=entries){
                if(currentRecord>=(nrecords-1)) return icounter;
                currentRecord++;
                try {
                    reader.readRecord(currentRecord);
                } catch (HipoException ex) {
                    Logger.getLogger(AbsDataRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
                position = 1;
                entries  = reader.getCurrentRecordStream().getEntries()-1;
            }
            int size = reader.getCurrentRecordStream().getEventLength(position);
            event.get(i).resize(size);
            try {
                reader.getCurrentRecordStream().getEvent(event.get(i).eventBuffer, 0, position);
                event.get(i).eventBuffer.putInt(8, size);
                event.get(i).setSchemaFactory(factory);
                position++;
                icounter++;
            } catch (HipoException ex) 
            { 
                Logger.getLogger(AbsDataRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return icounter;
    }
    
    public synchronized int pop(DataEventHipo event){
        
        if(position>=entries){            
            if(currentRecord>=(nrecords-1)) return 0;
            currentRecord++;
            try {
                reader.readRecord(currentRecord);
            } catch (HipoException ex) {
                Logger.getLogger(AbsDataRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
            position = 1;
            entries  = reader.getCurrentRecordStream().getEntries()-1;
        }
        //try { readsem.acquire(); } catch (InterruptedException e) { return 0;}

        int size = reader.getCurrentRecordStream().getEventLength(position);
        event.resize(size);
        try {
            reader.getCurrentRecordStream().getEvent(event.eventBuffer, 0, position);
            event.eventBuffer.putInt(8, size);
            event.setSchemaFactory(factory);
            position++;
            
        } catch (HipoException ex) 
        { 
            Logger.getLogger(AbsDataRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        //readsem.release();        
        return size;
    }
    
    public int pop(byte[] data){
         try { readsem.acquire(); } catch (InterruptedException e) { return 0;}
         // We need to acquire the semaphore so that this.head doesn't change.
         if(currentChunkCounter>=numberOfChunks) return 0;
         int position = chunkSize * currentChunkCounter;
         System.arraycopy(queueBuffer.array(), position, data, 0, chunkSize);        
         readsem.release();
         return chunkSize;
    }
    
    public static void main(String[] args){
        String inputFile = "/Users/gavalian/Work/DataSpace/clas12/mc/clas_dis_mcdata.hipo";
        Integer  ncores  = 4;
        AbsDataRecordQueue  queue  = new AbsDataRecordQueue(inputFile,100);
        List<DataEventHipo> data   = new ArrayList<DataEventHipo>();
        for(int i = 0; i < 20; i++){
            data.add(new DataEventHipo());
        }
        
        int nread = queue.pop(data);
        System.out.println(" first read = " + nread);
        int icounter = 0;
        while(nread!=0){            
           nread = queue.pop(data);
           icounter++;
        }
    }
}
