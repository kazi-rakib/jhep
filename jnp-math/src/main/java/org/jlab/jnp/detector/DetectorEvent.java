/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.detector;

import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public class DetectorEvent {
    
    private PhysicsEvent physEvent = new PhysicsEvent();
    
    public DetectorEvent(){
        
    }
    
    
    public PhysicsEvent getPhysicsEvent(){ return this.physEvent;}
    
    
}
