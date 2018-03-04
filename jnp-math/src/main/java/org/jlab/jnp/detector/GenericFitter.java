/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.detector;

import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.pdg.PDGDatabase;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;

/**
 *
 * @author gavalian
 */
public class GenericFitter {
    private double energy = 11.0;
    private String stringParticleBank = "REC::Particle";
    
    public GenericFitter(){
        
    }
    
    public void setEnergy(double en){
        this.energy = en;
    }
    private boolean checkVectors(HipoGroup group, int row){
        if(Float.isInfinite(group.getNode("px").getFloat(row))||
                Float.isNaN(group.getNode("px").getFloat(row))) return false;
        if(Float.isInfinite(group.getNode("py").getFloat(row))||
                Float.isNaN(group.getNode("py").getFloat(row))) return false;
        if(Float.isInfinite(group.getNode("pz").getFloat(row))||
                Float.isNaN(group.getNode("pz").getFloat(row))) return false;
        return true;
    }
    public void readEvent(HipoEvent event, PhysicsEvent physEvent){
        physEvent.clear();
        physEvent.setBeamParticle(new Particle(11,0.0,0.0,energy));
        physEvent.setTargetParticle(new Particle(2212,0.0,0.0,0.0));
        
        if(event.hasGroup(this.stringParticleBank)==true){
            HipoGroup group = event.getGroup(this.stringParticleBank);

            int rows = group.getMaxSize();
            for(int i = 0; i < rows; i++){
                if(checkVectors(group,i)==true){
                    Particle p = new Particle();
                int pid = group.getNode("pid").getInt(i);
                if(PDGDatabase.hasParticleById(pid)==true){                    
                    p.initParticle(pid, 
                            group.getNode("px").getFloat(i),
                            group.getNode("py").getFloat(i),
                            group.getNode("pz").getFloat(i),
                            group.getNode("vx").getFloat(i),
                            group.getNode("vy").getFloat(i),
                            group.getNode("vz").getFloat(i)
                            );
                } else {
                    p.initParticleWithPidMassSquare(0, group.getNode("charge").getByte(i), 1.39, 
                            group.getNode("px").getFloat(i),
                            group.getNode("py").getFloat(i),
                            group.getNode("pz").getFloat(i),
                            group.getNode("vx").getFloat(i),
                            group.getNode("vy").getFloat(i),
                            group.getNode("vz").getFloat(i));
                }
                physEvent.addParticle(p);
                }
            }
        }
    }
}
