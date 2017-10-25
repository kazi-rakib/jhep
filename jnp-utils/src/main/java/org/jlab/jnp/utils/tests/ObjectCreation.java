/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.utils.tests;

import org.jlab.jnp.utils.benchmark.Benchmark;

/**
 *
 * @author gavalian
 */
public class ObjectCreation {
    
    public void fillMatrix(double[][] matrix){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j ++){
                matrix[i][j] = 0.5*i+0.2*j;//Math.random();
            }
        }
    }
    
    public double[][] getMatrix(){
        double[][] matrix = new double[8][8];
        fillMatrix(matrix);
        return matrix;
    }
    
    public void resetMatrix(double[][] result){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j ++){
               result[i][j] = 0.0; 
            }
        }
    }
    public void multMatrix(double[][] a, double[][] b, double[][] result){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j ++){
                result[i][j] += (a[i][j]*b[j][i]);
            }
        }
    }
    
    public double[][] getMultMatrix(double[][] a, double[][] b){
        double[][] result = new double[8][8];
        multMatrix(a,b,result);
        return result;
    }
    
    public void processCreation(int count){
        for(int i = 0; i < count; i++){
            double[][] a = getMatrix();
            double[][] b = getMatrix();
            double[][] result = getMultMatrix(a,b);
        }
    }
    
    public void process(int count){
        double[][] a = new double[8][8];
        double[][] b = new double[8][8];
        double[][] r = new double[8][8];
        for(int i = 0; i < count; i++){
            this.fillMatrix(a);
            this.fillMatrix(b);
            this.multMatrix(a, b, r);
        }
    }
    
    public static void main(String[] args){
        
        int iterations = 100;
        int mult       = 25000;
        
        ObjectCreation processor = new ObjectCreation();
        Benchmark bench = new Benchmark();
        
        bench.addTimer("WARMUP-C");
        bench.addTimer("WARMUP-D");
        for(int i = 0; i < iterations; i++){
            bench.resume("WARMUP-C");
            processor.processCreation(mult);
            bench.pause("WARMUP-C");
        }
        for(int i = 0; i < iterations; i++){
            bench.resume("WARMUP-D");
            processor.process(mult);
            bench.pause("WARMUP-D");
        }
        System.out.println(bench.toString());
        
        Benchmark benchrun = new Benchmark();
        benchrun.addTimer("RUN-C");
        benchrun.addTimer("RUN-D");
        
        for(int i = 0; i < iterations; i++){
            benchrun.resume("RUN-C");
            processor.processCreation(mult*5);
            benchrun.pause("RUN-C");
        }
        for(int i = 0; i < iterations; i++){
            benchrun.resume("RUN-D");
            processor.process(mult*5);
            benchrun.pause("RUN-D");
        }
        System.out.println(benchrun.toString());
    }
}
