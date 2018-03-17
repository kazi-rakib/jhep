/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics.maps;

import java.util.HashMap;
import java.util.Map;
import org.jlab.jnp.hipo.abs.AbsDataMap;
import org.jlab.jnp.hipo.base.DataEvent;
import org.jlab.jnp.hipo.base.DataMap;
import org.jlab.jnp.hipo.base.DataMapProducer;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.EventSelector;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.reader.EventReader;

/**
 *
 * @author gavalian
 */
public class PhysicsMapProducer implements DataMapProducer {
    
    private PhysicsEvent                 physEvent = new PhysicsEvent();
    private Map<String,EventSelector>    selectors = new HashMap<String,EventSelector>();
    private AbsDataMap                     dataMap = new AbsDataMap();
    private EventFilter                eventFilter = null;// new EventFilter();
    
    public PhysicsMapProducer(){
        eventFilter = new EventFilter("11:2212:22:22:X+:X-:Xn");
        selectors.put("pi0m", new EventSelector("[22,0]+[22,1]"));
        selectors.put("w2", new EventSelector("[b]+[t]-[11]"));
        selectors.put("mpi0", new EventSelector("[b]+[t]-[11]-[2212]"));
        selectors.put("mp", new EventSelector("[b]+[t]-[11]-[22,0]-[22,1]"));
        for(Map.Entry<String,EventSelector> entry : selectors.entrySet()){
            dataMap.setValue(entry.getKey(), -9999.0);
        }
    }
    
    @Override
    public void createMap(DataEvent event, DataMap map) {
        
        if(map.getSize()!=selectors.size()){
            for(Map.Entry<String,EventSelector> entry : selectors.entrySet()){
                map.setValue(entry.getKey(), -9999.0);
            }
        }
        
        EventReader.readPhysicsEvent((DataEventHipo) event, physEvent, "mc::event");
        physEvent.beamParticle().initParticle(11, 0.0, 0.0, 10.6, 0, 0, 0);
        if(eventFilter.isValid(physEvent)==true){
            for(Map.Entry<String,EventSelector> entry : selectors.entrySet()){
                Particle p = entry.getValue().get(physEvent);
                map.setValue(entry.getKey(), p.mass());
            }
            map.setStatus(true);
        } else {
            map.setStatus(false);
        }
    }

    public static class PhysicsMapProducerBuilder {
                
        public PhysicsMapProducerBuilder(){
            
        }
    }
}
