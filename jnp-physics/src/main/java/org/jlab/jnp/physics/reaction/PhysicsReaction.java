/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics.reaction;

import java.util.Map;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.processes.SIDISEventGenerator;

/**
 *
 * @author gavalian
 */
public class PhysicsReaction {
    
    private ReactionGenerator   generator = null;
    private PhaseSpace         phaseSpace = null;
    private ReactionWeight   crossSection = null;
    
    public PhysicsReaction(){
        
    }
    
    public void setGenerator(ReactionGenerator gen){
        generator = gen;
    }
    
    public void setPhaseSpace(PhaseSpace ps){
        phaseSpace = ps; 
    }
    
    public void setReactionWeight(ReactionWeight w){
        crossSection = w;
    }
    
    public PhaseSpace   getPhaseSpace(){return phaseSpace;}
    
    public PhysicsEvent generate(){
        boolean validEvent = false;
        PhysicsEvent event = null;
        while(validEvent==false){
            phaseSpace.setRandom();
            Map<String,Double>  variables = phaseSpace.getMap();
            
            event = this.generator.createEvent(variables);
            if(event.getParticleList().count()>0){
                if(this.crossSection!=null){
                    double weight = this.crossSection.getWeight(variables);
                    event.setWeight(weight);
                }
                validEvent = true;
            }
        }
        return event;
    }
    
    public static void main(String[] args){
        SIDISEventGenerator generator = new SIDISEventGenerator();
        PhysicsReaction reaction = new PhysicsReaction();
        
        reaction.setGenerator(generator);
        PhaseSpace  phaseSpace = new PhaseSpace();
        phaseSpace.add(  "E",  11.0, 11.0);
        phaseSpace.add( "q2",   1.0,  4.0);
        phaseSpace.add( "xb", 0.025,  0.9995);
        phaseSpace.add(  "z", 0.025,  0.9995);
        phaseSpace.add( "pt",   0.2,  1.0);
        phaseSpace.add("phi", -Math.PI, Math.PI);
        
        reaction.setPhaseSpace(phaseSpace);
        SIDIS sidis = new SIDIS();
        
        for(int i = 0; i < 100; i++){
            PhysicsEvent event = reaction.generate();
            System.out.println(reaction.getPhaseSpace());
            System.out.print(event.toLundString());
            sidis.processPhysicsEvent(event);
            System.out.println(sidis.toString());
        }
        
    }
}
