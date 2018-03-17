/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.abs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.hipo.base.DataMap;

/**
 *
 * @author gavalian
 */
public class AbsDataMap implements DataMap {
    
    private boolean mapStatus = true;
    
    
    
    private Map<String,Double>     dataMap = new HashMap<String,Double>();
    private List<String>       dataMapKeys = null;
    
    @Override
    public void setStatus(boolean status){
        mapStatus = status;
    }
    
    @Override
    public List<String> getKeys() {
        //if(dataMapKeys==null){
            dataMapKeys = new ArrayList<String>();
            for(Map.Entry<String,Double> entry : dataMap.entrySet()){
                dataMapKeys.add(entry.getKey());
            }
        //}
        return dataMapKeys;
    }

    @Override
    public double getValue(String key) {
        return dataMap.get(key);
    }

    @Override
    public double getValue(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(String key, double value) {
        dataMap.put(key, value);
        //dataMap.replace(key, value);
    }

    @Override
    public void setValue(int index, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public boolean getStatus() {
        return mapStatus;
    }

    @Override
    public int getSize() {
        return dataMap.size();
    }
    
}
