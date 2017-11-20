/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.foam.MCFoam;
import org.jlab.jnp.math.data.DataAxis;
import org.jlab.jnp.math.data.DataVector;
import org.jlab.jnp.math.matrix.SparseMatrix;

import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.reader.EventReader;
import org.jlab.jnp.reader.EventWriter;
import org.jlab.jnp.reader.LundReader;
import org.jlab.jnp.utils.data.ArrayUtils;
import org.jlab.jnp.utils.options.OptionParser;

/**
 *
 * @author gavalian
 */
public class SIDISProcessor {
    
    SIDISReactionWeight func = new SIDISReactionWeight();
    PhaseSpace phaseSpace = new PhaseSpace();
    MCFoam foam = null;
    double[] unitValues = null;
    double[] physValues = null;
    
    public SIDISProcessor(){        
        phaseSpace.add("E",11.0,11.0,11.0);
        phaseSpace.add("q2", 1.2, 1.0, 1.4);
        phaseSpace.add("xb", 0.4, 0.05, 0.995);
        phaseSpace.add("z", 0.4, 0.05, 0.995);
        phaseSpace.add("pt", 0.4, 0.2, 1.0);
        phaseSpace.add("phi",-Math.PI,Math.PI);        
        func.setPhaseSpace(phaseSpace);
        foam = new MCFoam(func);
        unitValues = new double[phaseSpace.getMap().size()];
        physValues = new double[phaseSpace.getMap().size()];        
    }
    
    public void init(){
        long start_time = System.currentTimeMillis();
        foam.init();
        long end_time   = System.currentTimeMillis();
        
        double time_seconds = ((double) (end_time-start_time))*1000.0;
        System.out.println(String.format("[FOAM] Initialization Time = %.2f seconds",time_seconds));        
    }
    /**
     * Get Map of values randomly generated from the FOAM
     * @return 
     */
    public Map<String,Double> getValues(){
        Map<String,Double> psMap = this.phaseSpace.getMap();
        foam.getRandom(unitValues);
        phaseSpace.setUnit(unitValues);
        phaseSpace.getValues(physValues);
        Map<String,Double> map = new LinkedHashMap<String,Double>();
        int i = 0;
        for(Map.Entry<String,Double> entry : psMap.entrySet()){
            map.put(entry.getKey(), physValues[i]); i++;
        }
        return map;
    }
    
    public static void main(String[] args){
        
        SIDISProcessor processor = new SIDISProcessor();
        
        processor.init();
        
        for(int i = 0; i < 40; i++){
            Map<String,Double> values = processor.getValues();
            System.out.println(ArrayUtils.getMapStringWithKey(values, "%.5f", " "));
        }
        
        /*
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
            ps.add("q2", 1.0, 1.4);
            ps.add("xb", 0.0, 1.0);
            ps.add("z", 0.0, 1.0);
            ps.add("pt", 0.5, 1.0);
            ps.add("phi", -3.14, 3.14);
            
            SIDISProcessor.processMapProducer(inputs.get(0), ps);
        }
        */
        
    }
}
