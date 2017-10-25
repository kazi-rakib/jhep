/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics.map;

import java.util.Map;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public interface MapProducer {
    
    Map<String,Double>  getMap();
    boolean             processPhysicsEvent(PhysicsEvent event);
    
}
