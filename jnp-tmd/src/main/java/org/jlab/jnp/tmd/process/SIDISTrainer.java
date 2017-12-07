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
    public static int iterationCounterPrintout = 10000;
    public static long iterationTotalEvents = 0L;
    
    private DataSet trainingSet = null;
    
    public SIDISTrainer(){
        
    }
    
    
    public void train(){
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(
                TransferFunctionType.SIGMOID, 400, 20, 6);
        //myMlPerceptron.getLearningRule().setMaxIterations(40);
        myMlPerceptron.getLearningRule().setLearningRate(0.2);
        myMlPerceptron.getLearningRule().setMaxError(0.0001);
        
        SIDISTrainer.iterationCounter = 0;
        myMlPerceptron.addListener(new NeuralNetworkEventListener(){
            @Override
            public void handleNeuralNetworkEvent(NeuralNetworkEvent nne) {
                double error = myMlPerceptron.getLearningRule().getErrorFunction().getTotalError();
                
                SIDISTrainer.iterationCounter++;
                if(SIDISTrainer.iterationCounter>=SIDISTrainer.iterationCounterPrintout){
                    System.out.println("iteration : " + SIDISTrainer.iterationTotalEvents + " error = " + error);
                    SIDISTrainer.iterationCounter = 0;
                    SIDISTrainer.iterationTotalEvents++;
                }
                //System.out.println(myMlPerceptron);
                //System.out.println("event happened, type = " + nne.getEventType());                
            }
        });
        myMlPerceptron.learn(trainingSet);
        double error = myMlPerceptron.getLearningRule().getErrorFunction().getTotalError();
        System.out.println("error = " + error);
        
        myMlPerceptron.save("TMD_NN_20x20.nnet");
    }
    
    public void createTrainingSet(String filename){
        trainingSet = new DataSet(400,6);
        TextFileReader reader = new TextFileReader();
        reader.setSeparator(",");
        reader.open(filename);
        int counter = 0;
        int   lines = 0;
        int max = 400;
        while(reader.readNext()==true&&counter<max){
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
        trainer.createTrainingSet("data_sample_10x10.txt");
        trainer.train();
    }
}
