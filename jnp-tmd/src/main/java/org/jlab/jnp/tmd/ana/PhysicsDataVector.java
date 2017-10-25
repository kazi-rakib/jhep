/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.ana;

import java.util.Map;
import org.jlab.hep.math.data.DataVector;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.reader.EventReader;

/**
 *
 * @author gavalian
 */
public class PhysicsDataVector {
    
    private PhaseSpace phaseSpace = null;
    private String     vectorDim  = null;
    private DataVector<Double> dataVector = null;

    public PhysicsDataVector(String dv, PhaseSpace ps){
        vectorDim  = dv;
        phaseSpace = ps;
        phaseSpace.resetCounter();
        int nbins = phaseSpace.getDimension("phi").getNBins();
        dataVector = new DataVector<Double>(nbins,0.0);        
    }
    
    public void fill(Map<String,Double> values, double weight){
        if(phaseSpace.contains(values)==true){
            phaseSpace.addValues(values,weight);
             int bin = phaseSpace.getDimension(vectorDim).getBin(values.get(vectorDim));
             double prevValue = dataVector.valueOf(bin);
             dataVector.setValue(bin, prevValue+weight);
        }

    }
    
    public PhaseSpace getPhaseSpace(){
        return phaseSpace;
    }
    
    public void printData(){
        int nsize = this.dataVector.getSize();
        for(int i = 0; i < nsize; i++){
            System.out.println(String.format("%16.8f", dataVector.valueOf(i)));
        }
    }
    
    public static void main(String[] args){
        String file = "sidis_data.hipo";
        EventReader reader = new EventReader();
        reader.open(file);
        PhysicsEvent mcEvent = new PhysicsEvent();
        
        PhaseSpace space = new PhaseSpace();
        space.add("q2", 1.0, 1.6);
        space.add("xb", 0.2, 0.22);
        space.add("pt", 0.2, 0.22);
        space.add("z" , 0.2, 0.25);
        space.add("phi", -Math.PI, Math.PI);        
        space.getDimension("phi").setNBins(25);
        
        PhysicsDataVector vector = new PhysicsDataVector("phi",space);
        SIDIS             mapProducer = new SIDIS();
        int counter = 0;
        while(reader.nextEvent()==true){
            reader.getMcEvent(mcEvent);
            //System.out.println(mcEvent.toLundString());
            mapProducer.processPhysicsEvent(mcEvent);
            //System.out.println(mapProducer.toString());
            vector.fill(mapProducer.getMap(),mcEvent.getWeight());
            counter++;
            if(counter%25000==0) System.out.println("processed " + counter);
        }
        System.out.println(vector.getPhaseSpace().toString());
        vector.printData();
    }
}
