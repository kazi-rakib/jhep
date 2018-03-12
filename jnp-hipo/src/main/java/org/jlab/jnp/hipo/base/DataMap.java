/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.base;

import java.util.List;

/**
 *
 * @author gavalian
 */
public interface DataMap {
    
    public List<String> getKeys();
    public double getValue(  String key);
    public double getValue(   int index);
    public void   setValue( String key, double value);
    public void   setValue(  int index, double value);
    
}
