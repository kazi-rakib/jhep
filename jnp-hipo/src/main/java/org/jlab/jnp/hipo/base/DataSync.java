/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

/**
 *
 * @author gavalian
 */
public interface DataSync {
    
    public void configure(String config);
    public void write(DataEvent event, DataMap map);
    public void merge(DataSync sync);
    
}
