/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.task;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoWriter;

/**
 *
 * @author gavalian
 */
public class HipoTaskProcess {
    
    private List<Thread>       processTasks = new ArrayList<Thread>();
    private List<HipoRunnable>  processRuns = new ArrayList<HipoRunnable>();
    private HipoWriter               writer = null;
    private String               outputFile = null;
    
    
    public HipoTaskProcess(String outfile){
        this.outputFile = outfile;
    }
    public void addThread(HipoRunnable runnable){
        Thread t = new Thread(runnable);
        this.processRuns.add(runnable);
        processTasks.add(t);
    }
    
    private int checkTasks(){
        for(int i = 0; i < processTasks.size(); i++){
            if(processTasks.get(i).isAlive()==false) return i;
        }
        return -1;
    }
    
    public void run(int count){
        
        int completed = 0;
        int  nthreads = this.processTasks.size();
        writer = new HipoWriter();
        writer.open(outputFile);
        while(true){
            int id = this.checkTasks();
            if(completed==count&&id<0) break;
            if(id>=0){
                completed++;
                System.out.println(" starting task # " + id);
                //HipoEvent event = new HipoEvent();
                HipoEvent outEvent = processRuns.get(id).getOutput();
                if(outEvent!=null){
                    writer.writeEvent(outEvent);
                }
                
                HipoEvent event = writer.createEvent();
                this.processRuns.get(id).setInput(event);
                Thread t = new Thread(processRuns.get(id));
                this.processTasks.remove(id);
                this.processTasks.add(id, t);
                this.processTasks.get(id).start();
                //processTasks.get(id).interrupt();
                //this.processTasks.get(id).start();
            } else {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HipoTaskProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        writer.close();
        System.out.println("Completed all the tasks");
    }
    /**
     * Dummy class to test runnable interface.
     */
    static class DummyThread extends HipoRunnable {
        int sleepNumber = 100;
        public DummyThread(int sleep, int uid){
            this.sleepNumber = sleep;
            setUID(uid);
        }
        
        @Override
        public void processEvent(HipoEvent event) {
            int[] array = new int[15];
            HipoNode node = new HipoNode(200,1,array);
            event.addNode(node);
            try {
                Thread.sleep(sleepNumber);
            } catch (InterruptedException ex) {
                Logger.getLogger(HipoTaskProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static void main(String[] args){
        HipoTaskProcess task = new HipoTaskProcess("test_4.hipo");
        task.addThread(new DummyThread(10000,23));
        task.addThread(new DummyThread(35000,24));
        task.addThread(new DummyThread(50000,25));
        task.run(100);
    }
}
