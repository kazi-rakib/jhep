/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.readers.TextFileReader;
import org.jlab.jnp.readers.TextFileWriter;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.NeuralNetworkEvent;
import org.neuroph.core.events.NeuralNetworkEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.jlab.jnp.utils.data.ArrayUtils;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
/**
 *
 * @author gavalian
 */
public class SIDISTrainer {
    
    public static int iterationCounter = 0;
    public static int iterationCounterPrintout = 10000;
    public static long iterationTotalEvents = 0L;
    private int        maxIterations   = 5000;
    private double     maxNetworkError = 0.0001;
    private boolean    stopTraining = false;
    MultiLayerPerceptron myMlPerceptron = null;
    
    private DataSet trainingSet = null;
    
    public SIDISTrainer(){
        
    }
    
    public void check(){
        
    }
    
    public void setMaxIterations(int miter){
        this.maxIterations = miter;
    }
    
    public void setStopTraining(boolean flag){
        this.stopTraining = flag;
    }
    
    public void stopLearning(){
        double error = myMlPerceptron.getLearningRule().getErrorFunction().getTotalError();
        System.out.println("error = " + error);
        System.out.println("Writing file : TMD_NN_10x10x10.nnet");
        myMlPerceptron.save("TMD_NN_10x10x10.nnet");
    }
    
    public void train(){
        
        
        myMlPerceptron = new MultiLayerPerceptron(
                TransferFunctionType.SIGMOID, 1000, 40, 5);
        
        BackPropagation train = new BackPropagation();
        
        train.setLearningRate(0.15);
        train.setMaxIterations(maxIterations);
        train.setMaxError(0.0001);
        
        DynamicBackPropagation dtrain = new DynamicBackPropagation();
        dtrain.setMaxMomentum(0.6);
        dtrain.setMaxError(0.0001);
        dtrain.setMaxIterations(maxIterations);
        dtrain.setLearningRate(0.2);
        
        MomentumBackpropagation mtrain = new MomentumBackpropagation();
        
        mtrain.setMomentum(0.7);
        mtrain.setMaxError(0.0001);
        mtrain.setMaxIterations(maxIterations);
        mtrain.setLearningRate(0.2);
        //myMlPerceptron.getLearningRule().setMaxIterations(this.maxIterations);
        
        //myMlPerceptron.getLearningRule().setLearningRate(0.2);
        //myMlPerceptron.getLearningRule().setMaxError(0.0001);
        
        SIDISTrainer.iterationCounter = 0;
        myMlPerceptron.setLearningRule(mtrain);
        
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
                /*if(stopTraining==true) {
                    myMlPerceptron.stopLearning();
                    myMlPerceptron.save("TMD_NN_10x10x10.nnet");
                }*/
                
                //System.out.println(myMlPerceptron);
                //System.out.println("event happened, type = " + nne.getEventType());                
            }
        });
        System.out.println(" STARTING TRAINING ");
        double maxError = 1000.0;
        myMlPerceptron.learn(trainingSet);
        /*while(maxError>maxNetworkError&&stopTraining==false){
            myMlPerceptron.le
        }*/
        //double error = myMlPerceptron.getLearningRule().getErrorFunction().getTotalError();
        //System.out.println("error = " + error);
        //System.out.println("Writing file : TMD_NN_10x10x10.nnet");
        //myMlPerceptron.save("TMD_NN_10x10x10.nnet");
    }
    public void createTrainingSet(String filename, int nInput, int nOutput){
        trainingSet = new DataSet(nInput,nOutput);
        HipoReader reader = new HipoReader();
        reader.open(filename);
        int counter = 0;
        while(reader.hasNext()==true){
            HipoEvent event = reader.readNextEvent();
            HipoNode   nodeInput = event.getNode(200, 1);
            HipoNode  nodeOutput = event.getNode(200, 2);
            
            double[]  tr_in = nodeInput.getDouble();
            double[] tr_out = nodeOutput.getDouble();
            trainingSet.addRow(tr_in, tr_out);
            counter++;
        }
        System.out.println(" LOADED TRANING SAMPLES = " + counter);
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
    
    public void exportFile(String binaryInput, String textOutput){
        HipoReader reader = new HipoReader();
        reader.open(binaryInput);
        TextFileWriter writer = new TextFileWriter();
        writer.open(textOutput);
        while(reader.hasNext()){
            HipoEvent event = reader.readNextEvent();
            HipoNode  nodeI = event.getNode(200, 1);
            HipoNode  nodeO = event.getNode(200, 2);
            String inputText = ArrayUtils.getString(nodeI.getDouble(), ",");
            String outputText = ArrayUtils.getString(nodeO.getDouble(), ",");
            writer.writeString(inputText + "," + outputText);
        }
        writer.close();
    }
    public static void main(String[] args){
        
        //SIDISTrainer trainer = new SIDISTrainer();
        //trainer.exportFile("sidis_training_set.hipo", "input_trainer.data");
        
        String inputFile = args[0];
        Integer maxIterations  = Integer.parseInt(args[1]);
        
        SIDISTrainer trainer = new SIDISTrainer();
        
        trainer.setMaxIterations(maxIterations);
        trainer.createTrainingSet(inputFile,1000,5);
        
        
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              System.out.println("\n**** COUGHT INTERRUPT ****\n");
              System.out.println("Initiating clean shutdown.....");
              //trainer.setStopTraining(true);
              trainer.stopLearning();
              System.out.println("Finished the shutdown");
            }
        });
        trainer.train();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SIDISTrainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Done....");
    }
}
