/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataAxisSpace {
    
    public List<DataAxis> axisSpace = new ArrayList<DataAxis>();
    
    public DataAxisSpace(){
        
    }
    
    public void addAxis(DataAxis axis){
        DataAxis axisCopy = new DataAxis(axis.getName(),axis.getBins(),axis.getMin(),axis.getMax());
        axisCopy.setLog(axis.isLog());
        axisSpace.add(axis);
    }
    
    public DataAxis getAxis(int index){
        return this.axisSpace.get(index);
    }
    
    public List<DataAxis> getList(){
        return this.axisSpace;
    }
}
