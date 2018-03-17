/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.abs;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo.base.Builder;
import org.jlab.jnp.hipo.base.DataEvent;
import org.jlab.jnp.hipo.base.DataProcessor;

/**
 *
 * @author gavalian
 */
public class AbsDataWorker implements Runnable {
    
    protected final AbsDataRecordQueue      dataQueue;
    protected final DataProcessor       dataProcessor;
    protected final List<DataEvent>     dataEventCache = new ArrayList<>();
    protected final int                 dataEventCacheSize;
    protected int                       workerId       = 0;
    
    private AbsDataWorker(AbsDataRecordQueue queue, DataProcessor processor, int cacheSize) {
        dataQueue          = queue;
        dataProcessor      = processor;
        dataEventCacheSize = cacheSize;
    }
    
    @Override
    public void run() {
        boolean status = true;
        while(status==true){
           int result = dataQueue.nextEvents(dataEventCache, dataEventCacheSize);
           if(result!=0){
               for(int loop = 0; loop < result; loop++){
                   dataEventCache.get(loop).rehash();
                   try {
                       dataProcessor.processData(dataEventCache.get(loop));
                   } catch (Exception e) {
                       System.out.println("*** error *** processing event # " + loop + " failed misarably");
                   }
               }
           } else {
               status = false;
           }
        }
    }
    
    public int   getWorkerId(){ return this.workerId;}
    
    public void  setWorkerId(int id){ this.workerId = id;}
    
    public DataProcessor  getDataProcessor(){
        return this.dataProcessor;
    }
    
    public static class AbsDataWorkerBuilder implements Builder<AbsDataWorker> {
        
        private Builder<DataProcessor>  processor = null;
        private AbsDataRecordQueue      dataQueue = null;
        private int                     cache     = 1;
        
        public AbsDataWorkerBuilder processor(Builder<DataProcessor> pb){
            processor = pb;
            return this;
        }
        
        public AbsDataWorkerBuilder cache(int size){
            cache = size;
            return this;
        }
        
        public AbsDataWorkerBuilder dataQueue(AbsDataRecordQueue dq){
            dataQueue = dq;
            return this;
        }
        
        @Override
        public AbsDataWorker build() {
            DataProcessor dataProc = processor.build();
            return new AbsDataWorker(dataQueue,dataProc,cache);
        }
    }
}
