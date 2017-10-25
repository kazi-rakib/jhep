/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public class BaseMapProducer implements MapProducer {
    
    private Map<String,Double> bMap = new LinkedHashMap<String,Double>();
    private EventFilter        eventFilter = new EventFilter("X+:X-:Xn");
    private Map<String,MapProducerParticle> mapParticles = new LinkedHashMap<>();
    
    public BaseMapProducer(){
        
    }

    public void setFilter(String filter){
        eventFilter.setFilter(filter);
    }
    
    public void addParticle(String name, String operator){
        MapProducerParticle particle = new MapProducerParticle(name,operator);
        this.mapParticles.put(name, particle);
    }
    
    public void addProperty(String name, String property){
        mapParticles.get(name).getPropertyList().add(property);
    }
    
    @Override
    public Map<String, Double> getMap() {
        return bMap;
    }

    @Override
    public boolean processPhysicsEvent(PhysicsEvent event) {
        if(eventFilter.isValid(event)==false) return false;
        this.bMap.clear();
        for(Map.Entry<String,MapProducerParticle> entry : mapParticles.entrySet()){
            Particle p = event.getParticle( entry.getValue().getOperator() );
            for(String item : entry.getValue().getPropertyList()){
                bMap.put(entry.getKey() + "__" + item, p.get(item));
            }
        }
        return true;
    }
    
    public float[] getArray(){
        int size = bMap.size();
        float[] array = new float[size];
        int counter = 0;
         for(Map.Entry<String,Double> entry : bMap.entrySet()){
           //str.append(entry.getValue()).append("  ");
           array[counter] = entry.getValue().floatValue();
           counter++;
        }
         return array;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(Map.Entry<String,Double> entry : bMap.entrySet()){
           str.append(entry.getValue()).append("  ");
        }
        return str.toString();
    }
    
    
    public static class MapProducerParticle {
        String name = "";
        String operator = "";
        
        List<String>  properties = new ArrayList<String>();
        
        public MapProducerParticle(String __name, String __formulae){
            name = __name;
            operator = __formulae;
        }
        
        public String getName(){ return name;}
        public String getOperator(){
            return operator;
        }
        
        public void addProperty(String prop){
            properties.add(prop);
        }
        
        public List<String> getPropertyList(){return properties;}
        
    }
}
