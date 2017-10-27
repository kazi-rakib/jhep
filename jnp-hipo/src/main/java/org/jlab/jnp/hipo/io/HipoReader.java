/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import org.jlab.jnp.hipo.data.HipoEvent;

/**
 * HipoReader class based on combined EVIO-6/HIPO format.
 * @author gavalian
 */
public class HipoReader {
    public HipoReader(){
        
    }
    
    public void open(String filename){
        
    }
    
    public int getEventCount(){
        return 1;
    }
    
    public HipoEvent readEvent(int index){
        return new HipoEvent();
    }
}
