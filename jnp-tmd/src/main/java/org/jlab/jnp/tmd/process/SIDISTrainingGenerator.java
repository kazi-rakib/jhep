/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import org.jlab.groot.data.H2F;
import org.jlab.jnp.foam.MCFoam;
import org.jlab.jnp.math.data.Parameters;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.readers.TextFileWriter;
import org.jlab.jnp.utils.data.ArrayUtils;

/**
 *
 * @author gavalian
 */
public class SIDISTrainingGenerator {
    
    private SIDISReactionWeight cross = new SIDISReactionWeight();
    private PhaseSpace reactionSpace = new PhaseSpace();
    private int        numberOfEvents = 1000;
    private String     outputFileName = "data_sample_10x10.txt";
    private TextFileWriter outputStream = new TextFileWriter();
    private double[]        trainingInput = null;
    private double[]       trainingOutput = null;
    
    public SIDISTrainingGenerator(){
        
    }
    
    
    public void setPhaseSpace(PhaseSpace space){
        reactionSpace = space.copy();
        cross.setPhaseSpace(space);
    }
    
    public void setNumberOfEvents(int nev){
        this.numberOfEvents = nev;
    }
    
    public void generateSamples(int nsamples){
        outputStream.open(outputFileName);
        for(int i = 0; i < nsamples; i++){
            System.out.println("\n\n STARTING ITERATION " + i);
            generateSample();
           
            outputStream.writeString(
                    ArrayUtils.getString(trainingInput, "%e",",")+","+
                            ArrayUtils.getString(trainingOutput, "%e", ","));
            System.out.println("\n\n ITERATION " + i + " is done ");
        }
        outputStream.close();
    }
    
    public void generateSample(){
        Parameters pars = cross.getObservables().getParameters();
        pars.setRandom();
        
        this.trainingOutput = pars.getAsUnitArray();
        
        System.out.println(pars);
        cross.getObservables().setParameters(pars);
        H2F h2_PTZ = new H2F("h2_PTZ",20,0.0,1.0,20,0.0,1.0);
        
        MCFoam foam = new MCFoam(this.cross);   
        foam.init();
        
        double[] unitValues = new double[reactionSpace.getKeys().size()];
        double[] physValues = new double[reactionSpace.getKeys().size()];
        
        for(int i = 0; i < this.numberOfEvents; i++){
            foam.getRandom(unitValues);
            reactionSpace.setUnit(unitValues);
            reactionSpace.getValues(physValues);
            h2_PTZ.fill(reactionSpace.getDimension("pt").getValue(),reactionSpace.getDimension("z").getValue());
        }
        this.trainingInput = new double[h2_PTZ.getDataBufferSize()];
        double max = 0.0;
        for(int i = 0; i < trainingInput.length; i++){
            trainingInput[i] = h2_PTZ.getDataBufferBin(i);
            if(trainingInput[i]>max) max = trainingInput[i];
        }
        for(int i = 0; i < trainingInput.length; i++){
            trainingInput[i] = trainingInput[i]/max;
        }
        
        //System.out.println("DATA : " + ArrayUtils.getString(buffer, "%e", " "));
        //System.out.println("PARS : " + ArrayUtils.getString(parValues, "%e", " "));        
    }
    
    
    public static void main(String[] args){
        SIDISTrainingGenerator generator = new SIDISTrainingGenerator();
        PhaseSpace reactionPhaseSpace = new PhaseSpace();
        
        reactionPhaseSpace.add("E", 11.0,11.0);
        reactionPhaseSpace.add("q2", 1.0,1.1);
        reactionPhaseSpace.add("xb", 0.15,0.2);
        reactionPhaseSpace.add("z", 0.0,1.0);
        reactionPhaseSpace.add("pt", 0.0,3.0);
        reactionPhaseSpace.add("phi", -Math.PI,Math.PI);
        
        generator.setPhaseSpace(reactionPhaseSpace);
        generator.setNumberOfEvents(200000);
        
        /*for(int i = 0; i < 5; i ++){
            generator.generateSample();
        }*/
        generator.generateSamples(100);
        
    }
}
