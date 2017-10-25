/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import org.jlab.jhep.utils.options.OptionParser;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.physics.reaction.PhysicsReaction;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.processes.SIDISEventGenerator;
import org.jlab.jnp.reader.EventWriter;
import org.jlab.jnp.reader.LundWriter;

/**
 *
 * @author gavalian
 */
public class SIDISMonteCarlo {
    
    public static void generateEvents(String filename, int nevents){
        SIDISEventGenerator generator = new SIDISEventGenerator();
        PhysicsReaction reaction = new PhysicsReaction();
        SIDISReactionWeight weight = new SIDISReactionWeight();
                
        weight.loadResources();
        
        reaction.setGenerator(generator);
        reaction.setReactionWeight(weight);
        
        PhaseSpace  phaseSpace = new PhaseSpace();

        phaseSpace.add(   "E",  11.0, 11.0);
        phaseSpace.add(  "q2",   1.0,  10.0);
        phaseSpace.add(  "xb", 0.025,  0.9995);
        phaseSpace.add(   "z", 0.025,  0.9995);
        phaseSpace.add(  "pt",   0.2,  1.0);
        phaseSpace.add( "phi", -Math.PI, Math.PI);
        
        reaction.setPhaseSpace(phaseSpace);
        SIDIS sidis = new SIDIS();
        //EventWriter writer = new EventWriter(filename);
        LundWriter writer = new LundWriter(filename,20000);
        
        for(int i = 0; i < nevents; i++){
            PhysicsEvent event = reaction.generate();
            //System.out.println(reaction.getPhaseSpace());
            //System.out.print(event.toLundString());
            //sidis.processPhysicsEvent(event);
            //System.out.println(sidis.toString());
            //writer.reset();
            //writer.appendMcEvent(event);
            //writer.write();
            writer.writeEvent(event.toLundString());
        }
        writer.close();
    }
    
    public static void generateGrid(String inputFile, String outputFile, String phaseSpace){
        
    }
    
    
    public static void main(String[] args){
       OptionParser parser = new OptionParser();
       
       parser.addOption("-n", "1000", "number of events");
       //parser.addRequired("-", "analysis mode (0 - create hipo file from LUND)");
       parser.addRequired("-o", "output file name");        
       parser.parse(args);
       
       int nEvents = parser.getOption("-n").intValue();
       String outputFile = parser.getOption("-o").stringValue();
       
       SIDISMonteCarlo.generateEvents(outputFile, nEvents);
    }
}
