/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.hipo.abs.AbsDataMap;
import org.jlab.jnp.hipo.abs.AbsDataMapReducer;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.maps.PhysicsMapProducer;

/**
 *
 * @author gavalian
 */
public class AbsDataEventProcessor {
    
    AbsDataRecordQueue queue = null;
    List<Thread> threads = new ArrayList<Thread>();
    
    public AbsDataEventProcessor(String file, int record){
        queue = new AbsDataRecordQueue(file,record);
    }
    
    
    public void startProcessing(int nth) {
        this.threads.clear();
        for(int i = 0; i < nth; i++){
            Thread cth = new Thread(new Worker(queue,i,40));
            threads.add(cth);
        }
        long start_time = System.currentTimeMillis();
        for(int i = 0; i < nth; i++){
            threads.get(i).start();
        }
        
        boolean isRunning = true;
        int      nRunning = 0;
        while(isRunning == true ){
            
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
    
    public static class WorkerThread extends Thread {
        
    }
    
    public static class Worker implements Runnable {

        AbsDataRecordQueue workerQueue = null;
        DataEventHipo      hipoEvent   = new DataEventHipo();
        int                iterations  = 0;
        int                workerid    = 0;        
        PhysicsEvent       physEvent   = new PhysicsEvent();
        List<DataEventHipo> data   = new ArrayList<DataEventHipo>();
        
        PhysicsMapProducer mapProducer = new PhysicsMapProducer();
        AbsDataMap   dataMap = new AbsDataMap();
        AbsDataMapReducer reducer = new AbsDataMapReducer("mpi0>0.05&&mpi0<0.25",
                new String[] {"pi0m","w2","mpi0","mp"} );
        
        int icountPositive = 0;
        
        public Worker(AbsDataRecordQueue queue, int wid, int cache){
            workerQueue = queue;
            workerid = wid;
            for(int i = 0; i < 20; i++){
            data.add(new DataEventHipo());
        }
        }
        
        @Override
        public void run() {
            boolean status = true;
            while(status==true){
                //int read = workerQueue.pop(hipoEvent);
                int read = workerQueue.pop(data);
                if(read==0) {
                    status = false;
                } else {
                    for(int i = 0; i < read; i++){
                        data.get(i).updateIndex();
                        //EventReader.readPhysicsEvent(hipoEvent, physEvent, "mc::event");
                        mapProducer.createMap(data.get(i), dataMap);
                        if(dataMap.getStatus()==true){
                            boolean rst = reducer.reduce(dataMap);
                            if(rst==true) icountPositive++;
                        }
                        iterations++;
                    } 
                }
            }
            this.show();
        }
        
        public void show(){
            System.out.println("worker #  " + workerid + " iteration = " + iterations 
                    + " particles found = " + icountPositive
            );//String.format("worker # %4d  iterations = %5d", workerid, iterations));
        }
        
    }
    public static void main(String[] args){
        String inputFile = "/Users/gavalian/Work/DataSpace/clas12/mc/clas_dis_mcdata.hipo";
        Integer  ncores  = 4;
        
        int numCores = Runtime.getRuntime().availableProcessors();
        
        System.out.println(" Available cores = " + numCores);
        if(args.length>0){
            inputFile = args[0];
            ncores    = Integer.parseInt(args[1]);
        }
        for(int i = 0; i < 4; i++){
            AbsDataEventProcessor processor = new AbsDataEventProcessor(inputFile,1);            
            processor.startProcessing(numCores);
        }
    }
}
