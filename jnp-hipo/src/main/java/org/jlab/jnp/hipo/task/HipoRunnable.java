/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.task;

import java.io.OutputStream;
import java.io.PrintStream;
import org.jlab.jnp.hipo.data.HipoEvent;

/**
 *
 * @author gavalian
 */
public abstract class HipoRunnable implements Runnable {

    private HipoEvent     inputEvent = null;
    private HipoEvent    outputEvent = null;
    private long       executionTime = 0L;
    private boolean     maskPrintout = false;
    private int             uniqueID = 1;
    
    @Override
    public void run() {
        
        if(inputEvent==null){
            System.out.println("[HipoRunnable::thread] --> no input event was assigned.. exiting");
            return;
        }
        
        long  start_time = System.currentTimeMillis();
        try {
            PrintStream originalStream;
            originalStream = System.out;
            if(maskPrintout==true){
                //originalStream = System.out;
                PrintStream dummyStream = new PrintStream(new OutputStream(){
                    public void write(int b) {
                        // NO-OP
                    }
                });
                System.setOut(dummyStream);
            }
            processEvent(inputEvent);
            
            if(maskPrintout==true){
                System.setOut(originalStream);
            }
        } catch (Exception e) {
            System.out.println("[HipoRunnable::thread] --> error accured during execution of thread id="+uniqueID);
        }    
        long    end_time = System.currentTimeMillis();
        executionTime = end_time - start_time;
        outputEvent = inputEvent;
        inputEvent  = null;
        System.out.println("task # " + uniqueID + " is done");
    }
    
    public void setOutputMask(boolean flag){
        this.maskPrintout = flag;
    }
    
    public final void setUID(int uid){
        this.uniqueID = uid;
    }
    
    public long getExecutionTime(){ return this.executionTime;}
    
    public int getUID(){ return this.uniqueID;}
    
    public void setInput(HipoEvent event){
        inputEvent = event;
    }
    
    public void setOutput(HipoEvent event){
        outputEvent = event;
    }
    
    public HipoEvent getOutput(){ return outputEvent;}
    
    abstract public void processEvent(HipoEvent event);
       
}
