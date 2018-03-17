/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.hipo.abs.AbsDataWorker.AbsDataWorkerBuilder;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.utils.benchmark.Benchmark;

/**
 *
 * @author gavalian
 */
public class AbsDataEventProcessor {
    
    AbsDataRecordQueue        queue = null;
    List<Thread>            threads = new ArrayList<Thread>();    
    List<AbsDataWorker>     workers = new ArrayList<AbsDataWorker>();
    
    public AbsDataEventProcessor(AbsDataWorkerBuilder builder){
        int nProcessors = Runtime.getRuntime().availableProcessors()/2;
        if(nProcessors<1) nProcessors = 1;
        System.out.println("*** data-event-processor *** available processors = " + nProcessors);
        for(int i = 0; i < nProcessors; i++){
            AbsDataWorker worker = builder.build();
            worker.setWorkerId(i+1);
            workers.add(worker);
            Thread cth = new Thread(worker);
            threads.add(cth);
        }
    }
    
    public AbsDataEventProcessor(AbsDataWorkerBuilder builder, int ncores){
        int nProcessors = Runtime.getRuntime().availableProcessors()/2;
        if(nProcessors<1) nProcessors = 1;
        System.out.println("*** data-event-processor *** available processors = " 
                + nProcessors + " using " + ncores);
        for(int i = 0; i < ncores; i++){
            AbsDataWorker worker = builder.build();
            worker.setWorkerId(i+1);
            workers.add(worker);
            Thread cth = new Thread(worker);
            threads.add(cth);
        }
    }
    
    
    public List<AbsDataWorker> getWorkers(){
        return this.workers;
    }
    
    public void startProcessing(int nth) {
        
        long start_time = System.currentTimeMillis();
        for(int i = 0; i < nth; i++){
            threads.get(i).start();
        }
        
        boolean isRunning = true;
        int      nRunning = 0;
        while(  isRunning == true ){
            
            nRunning = 0;
            for(int i = 0; i < nth; i++){
                if(threads.get(i).isAlive()==true) nRunning++;
            }
            if(nRunning==0) isRunning = false;
            try{   
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(AbsDataEventProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        long end_time = System.currentTimeMillis();
        long time = end_time - start_time;
        System.out.println(" All Done..... running time = " + time);
    }
    
    public static class Worker implements Runnable {

        AbsDataRecordQueue  workerQueue = null;
        DataEventHipo       hipoEvent   = new DataEventHipo();
        int                 iterations  = 0;
        int                 workerid    = 0;        
        
        public Worker(AbsDataRecordQueue queue, int wid){
            workerQueue = queue;
            workerid = wid;
        }
        
        @Override
        public void run() {
            boolean status = true;
            while(status==true){
                int read = workerQueue.pop(hipoEvent);
                if(read==0) {
                    status = false;
                } else {
                    hipoEvent.updateIndex();
                    
                    iterations++;
                }                
            }
            this.show();
        }
        
        public void show(){
            System.out.println("worker #  " + workerid + " iteration = " + iterations 
            );//String.format("worker # %4d  iterations = %5d", workerid, iterations));
        }
        
    }
    public static void main(String[] args){
        
        /*Unsafe unsafe = sun.misc.Unsafe.getUnsafe();
        long startAdress = unsafe.allocateMemory(1024);
        */
        //AbsDataEventProcessor processor = new AbsDataEventProcessor("/Users/gavalian/Work/DataSpace/clas12/mc/clas_dis_mcdata.hipo",3);
        //processor.startProcessing(8);
    }
}
