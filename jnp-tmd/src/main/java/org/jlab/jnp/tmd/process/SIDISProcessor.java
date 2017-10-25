/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.List;
import java.util.Map;
import org.jlab.hep.math.data.DataAxis;
import org.jlab.hep.math.data.DataVector;
import org.jlab.jhep.math.matrix.SparseMatrix;
import org.jlab.jhep.utils.options.OptionParser;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.reader.EventReader;
import org.jlab.jnp.reader.EventWriter;
import org.jlab.jnp.reader.LundReader;

/**
 *
 * @author gavalian
 */
public class SIDISProcessor {
    
    public SIDISProcessor(){
        
    }
    
    public void createGrid(String eventFile, String matrixFile){
        
        SparseMatrix matrix = new SparseMatrix(new String[]{"generated","reconstructed","acceptance"});
        
        matrix.initAxis(new DataAxis("q2",40,1.0,2.5), 
                new DataAxis("xb",40,0.25,1.0),
                new DataAxis("y",40,0.25,1.0),
                new DataAxis("z",40,0.25,1.0),
                new DataAxis("pt",40,0.0,2.0),
                new DataAxis("phi",40,-3.14,3.14)
        );
        
        EventReader  reader = new EventReader();
        reader.open(eventFile);
        SIDIS sidis = new SIDIS();
        PhysicsEvent   mcEvent = new PhysicsEvent();
        PhysicsEvent  recEvent = new PhysicsEvent();
        
        while(reader.nextEvent()==true){
            reader.getMcEvent(mcEvent);
            reader.getDataEvent(recEvent);
            if(sidis.processPhysicsEvent(mcEvent)==true){
                //System.out.println(mcEvent.toLundString());
                System.out.println(sidis.toString());
                matrix.fill(0, sidis.getMap(), 1.0);
            }
            if(sidis.processPhysicsEvent(recEvent)==true){
                matrix.fill(1, sidis.getMap(), 1.0);
                //System.out.println("---> REC");
                //System.out.println(recEvent.toLundString());
                //System.out.println(sidis.toString());
            }
        }
        
        System.out.println( "Matrix SIZE = " + matrix.getMatrixMap().size());
        matrix.show();
    }
    
    public void createFile(List<String> lundFile, String hipoFile){

        PhysicsEvent generated = new PhysicsEvent();
        EventWriter writer = new EventWriter(hipoFile);
        int counter = 0;
        
        for(String file : lundFile){
            counter++;
            double fraction = ( (double) counter) /lundFile.size();
            System.out.println(String.format("[OPEN FILE] >>> %.2f ---> %s", fraction*100, file));
            LundReader reader = new LundReader();
            reader.addFile(file);
            reader.open();
            while(reader.next()){
                reader.nextEvent(generated);
                //System.out.println(generated.toLundString());
                PhysicsEvent mc = PhysicsEvent.filterEvent(generated, 21,1);
                //System.out.println(mc.toLundString());
                PhysicsEvent data = PhysicsEvent.filterEvent(generated, 2);
                //System.out.println(data.toLundString());
                writer.reset();
                writer.appendMcEvent(mc);
                if(data.getParticleList().count()>0){
                    writer.appendDataEvent(data);
                }
                writer.write();
            }
        }
        writer.close();
    }
    
    public static void processMapProducer(String filename, String phaseSpaceJsonFile){
       PhaseSpace phaseSpace = new PhaseSpace();
       phaseSpace.initJsonFile(phaseSpaceJsonFile);
    }
            
    public static void processMapProducer(String filename, PhaseSpace phaseSpace){
        
        SIDIS mapProducer  = new SIDIS();
        
        EventReader reader = new EventReader();
        reader.open(filename);
        PhysicsEvent mcEvent = new PhysicsEvent();
        int psRejected = 0;
        int psProduced = 0;
        
        DataAxis axisPhi = new DataAxis("phi",35,0.0,2.0*Math.PI);
        DataVector<Double> vector = new DataVector<Double>(35,0.0);
        
        while(reader.nextEvent()==true){
            reader.getMcEvent(mcEvent);
            //System.out.println(mcEvent.toLundString());
            mapProducer.processPhysicsEvent(mcEvent);
            Map<String,Double> map = mapProducer.getMap();
            if(phaseSpace.contains(map)==false) psRejected += 1;
        
            psProduced += 1;
            
            if(phaseSpace.contains(map)==true){
                double phi = Math.PI + map.get("phi");
               int bin = axisPhi.findBin(phi);
               if(bin>=0){
                   double value = vector.valueOf(bin);
                   vector.setValue(bin, value + mcEvent.getWeight());
               }
            }
            //mapProducer.toString()
            //System.out.println(mapProducer.toString());
        }
        
        System.out.println(" PROCESSED " + psProduced + "  REJECTED = " + psRejected);
        System.out.println("\n\n\n### DATA \n\n");
        for(int i = 0; i < axisPhi.getBins(); i++){
            System.out.println(String.format("%3d %15e %15e",i, axisPhi.binCenter(i),vector.valueOf(i)));
        }
    }
    
    public static void main(String[] args){
        
        OptionParser parser = new OptionParser();
        
        parser.addRequired("-mode", "analysis mode (0 - create hipo file from LUND)");
        parser.addRequired("-o", "output file name");
        parser.addRequired("-ps", "Phase space file");
        //parser.addRequired("-ps", "Phase space file");
        
        parser.parse(args);
        
        if(parser.getOption("-mode").intValue()==0){
            List<String> inputs = parser.getInputList();
            SIDISProcessor processor = new SIDISProcessor();
            processor.createFile(inputs, parser.getOption("-o").stringValue());
        }
        
        if(parser.getOption("-mode").intValue()==1){
            List<String> inputs = parser.getInputList();
            //SIDISProcessor processor = new SIDISProcessor();
            //processor.createGrid(inputs.get(0), "aa.hipo");
            String phaseSpaseJson = parser.getOption("-ps").stringValue();
            PhaseSpace ps = new PhaseSpace();
            ps.initJsonFile(phaseSpaseJson);
            /*ps.add("q2", 1.0, 1.4);
            ps.add("xb", 0.0, 1.0);
            ps.add("z", 0.0, 1.0);
            ps.add("pt", 0.5, 1.0);
            ps.add("phi", -3.14, 3.14);
            */
            SIDISProcessor.processMapProducer(inputs.get(0), ps);
        }
        
        
    }
}
