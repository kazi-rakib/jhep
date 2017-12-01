/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import org.jlab.jnp.readers.TextFileReader;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.NeuralNetworkEvent;
import org.neuroph.core.events.NeuralNetworkEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.jlab.jnp.utils.data.ArrayUtils;
/**
 *
 * @author gavalian
 */
public class SIDISTrainer {
    
    public static int iterationCounter = 0;
    public static int iterationCounterPrintout = 1000;
    
    private DataSet trainingSet = null;
    
    public SIDISTrainer(){
        
    }
    
    
    public void train(){
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(
                TransferFunctionType.GAUSSIAN, 400, 300,200,100, 6);
        myMlPerceptron.getLearningRule().setMaxIterations(400000);
        myMlPerceptron.getLearningRule().setLearningRate(0.2);
                
        SIDISTrainer.iterationCounter = 0;
        myMlPerceptron.addListener(new NeuralNetworkEventListener(){
            @Override
            public void handleNeuralNetworkEvent(NeuralNetworkEvent nne) {
                double error = myMlPerceptron.getLearningRule().getErrorFunction().getTotalError();
                SIDISTrainer.iterationCounter++;
                if(SIDISTrainer.iterationCounter>SIDISTrainer.iterationCounterPrintout){
                    System.out.println("iteration : " + SIDISTrainer.iterationCounter + " error = " + error);
                    SIDISTrainer.iterationCounter = 0;
                }
                //System.out.println(myMlPerceptron);
                //System.out.println("event happened, type = " + nne.getEventType());                
            }
        });
        myMlPerceptron.learn(trainingSet);
        double error = myMlPerceptron.getLearningRule().getErrorFunction().getTotalError();
        System.out.println("error = " + error);
    }
    
    public void createTrainingSet(String filename){
        trainingSet = new DataSet(400,6);
        TextFileReader reader = new TextFileReader();
        reader.setSeparator(",");
        reader.open(filename);
        int counter = 0;
        int   lines = 0;
        while(reader.readNext()==true){
            lines++;
            int size = reader.entrySize();
            if(size>=406){
                double[]  input = reader.getAsDoubleArray(0, 400);
                /*for(int i = 0; i < input.length; i++){
                    input[i] = input[i]/8000.0;
                }*/
                double[] output = reader.getAsDoubleArray(400, 6);
                System.out.println(ArrayUtils.getString(input, " "));
                System.out.println(ArrayUtils.getString(output, " "));

                trainingSet.addRow(input, output);
                counter++;
            } else {
                System.out.println(" SIZE = " + size);
            }
        }
        System.out.println("TRAINING SET LOADED. COUNT = " + counter + "  LINES READ = " + lines);
    }
    
    public static void main(String[] args){
        SIDISTrainer trainer = new SIDISTrainer();
        trainer.createTrainingSet("data_sample_20x20.txt");
        trainer.train();
    }
}
