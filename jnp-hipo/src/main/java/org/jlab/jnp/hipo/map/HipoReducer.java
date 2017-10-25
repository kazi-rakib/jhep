/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.map;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo.io.HipoReader;

/**
 *
 * @author gavalian
 */
public class HipoReducer {
    
    private HipoReader          reader = new HipoReader();
    private List<MapReducer>  reducers = new ArrayList<MapReducer>();
    
    public HipoReducer(){
        
    }
    
    public void open(String filename){
        reader.open(filename);
    }
    
    public void addMapReducer(MapReducer reducer){
        reducers.add(reducer);
    }
    
    public void goNext(){
        //HipoEvent event = reader.
    }
}
