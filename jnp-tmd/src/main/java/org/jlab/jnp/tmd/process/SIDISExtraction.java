/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.neuroph.core.NeuralNetwork;

/**
 *
 * @author gavalian
 */
public class SIDISExtraction {
    
    NeuralNetwork loadedMlPerceptron = null;
    
    List<GraphErrors>  resultGraphs = new ArrayList<GraphErrors>();
    private TCanvas    canvas  = null;
    
    public SIDISExtraction(){
        loadedMlPerceptron = NeuralNetwork.createFromFile("etc/data/TMD_20x20.nnet");
    }
    
    public double[] result(double[] inputData){
        loadedMlPerceptron.setInput(inputData);
        loadedMlPerceptron.calculate();
        return loadedMlPerceptron.getOutput();
    }
    
    public void init(){
        resultGraphs.clear();
        canvas = new TCanvas("extraction",600,600);
        canvas.divide(3, 2);
        
        for(int i = 0; i < 6; i++){
            GraphErrors graph = new GraphErrors("Parameter " + i);
            resultGraphs.add(graph);
            canvas.cd(i);
            canvas.draw(graph);
        }
    }
    
    public void fill(double[] src, double[] result){
        for(int i = 0; i < resultGraphs.size(); i++){
            resultGraphs.get(i).addPoint(src[i], result[i], 0.0, 0.0);
        }
        canvas.getCanvas().update();
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
        SIDISExtraction extraction = new SIDISExtraction();
        extraction.init();
        
        for(int i = 0; i < 500; i++){
            generator.generateSample();
            Map<Integer,double[]> map = generator.getMap();
            double[] result = extraction.result(map.get(1));
            extraction.fill(result, map.get(2));
        }
    }
}
