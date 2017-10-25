/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.ana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.physics.reaction.PhaseSpace;

/**
 *
 * @author gavalian
 */
public class PhysicsDataSet {
    private List<PhysicsDataVector> dataVectors = new ArrayList<PhysicsDataVector>();
    
    public PhysicsDataSet(){
        
    }
    
    public void add(String var, PhaseSpace ps){
        dataVectors.add(new PhysicsDataVector(var,ps));
    }
    
    public List<PhysicsDataVector> getDataVectors(){
        return this.dataVectors;
    }
    
    public void fill(Map<String,Double> values, double weight){
        for(PhysicsDataVector vec : this.dataVectors){
            vec.fill(values, weight);
        }
    }
}
