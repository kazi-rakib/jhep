/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.abs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.coda.hipo.HipoException;
import org.jlab.coda.hipo.Reader;
import org.jlab.jnp.hipo.base.DataEvent;
import org.jlab.jnp.hipo.base.DataRecord;
import org.jlab.jnp.hipo.base.DataSource;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.utils.benchmark.BenchmarkTimer;

/**
 *
 * @author gavalian
 */
public class AbsDataRecordQueue implements DataRecord {
    
    protected ByteBuffer   queueBuffer = null;
    private   Semaphore    readsem     = null;
    
    private int       position = 1;
    private int        entries = 0;    
    private int       currentChunkCounter = 0;
    private int       chunkSize           = 0;
    private int       numberOfChunks      = 0;
    private BenchmarkTimer  dataTimer     = new BenchmarkTimer("DataRecordQueue");
    
    
    Reader            reader              = null;
    
    
    public AbsDataRecordQueue(String filename){
        reader = new Reader(filename, true);
        //reader.open(filename); 
    }
    
    public synchronized int pop(DataEventHipo event){
        
        if(position>=entries) return 0;
        //try { readsem.acquire(); } catch (InterruptedException e) { return 0;}

        int size = reader.getCurrentRecordStream().getEventLength(position);
        event.resize(size);
        try {
            reader.getCurrentRecordStream().getEvent(event.eventBuffer, 0, position);
            event.eventBuffer.putInt(8, size);
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

    @Override
    public int readRecord(DataSource source, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int readEvent(DataEvent event, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextEvent(DataEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized int nextEvents(List<DataEvent> evList, int maxCount) {
        int readSize = 0;
        
        return readSize;
    }

    @Override
    public int getEventCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
