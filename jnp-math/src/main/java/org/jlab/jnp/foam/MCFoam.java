/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.foam;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.utils.data.ArrayUtils;

/**
 *
 * @author gavalian
 */
public class MCFoam {
    private List<MCell>  mcCells = new ArrayList<MCell>();
    private List<Double> mcCellWeights = new ArrayList<Double>();
    private IMCFunc      mcFunction    = null;
    private long         mcGenerationMisses = 0;
    private long         mcGenerationTotal  = 0;
    
    public MCFoam(){
        
    }
    
    public MCFoam(IMCFunc function){
        mcFunction = function;
        MCell mcell = new MCell(mcFunction.getNDim());
        mcCells.add(mcell);
    }
    /**
     * explores the maximum weight for the cell by random sampling.
     * @param cell cell to explore
     * @param func function providing density function
     * @param maxIter maximum iterations
     */
    public void exploreCellWeight(MCell cell, IMCFunc func, int maxIter){
        cell.setWeight(0.0);
        double[]  values = new double[cell.getDim()];
        for(int i = 0; i < maxIter; i++){
            cell.random(values);
            double weight = func.getWeight(values);
            if(weight<0.0){
                System.out.println("*** error *** function value is negative for : " + 
                        ArrayUtils.getString(values,"%5f", ","));
            }
            
            if(weight > cell.getWeight()) cell.setWeight(weight);
        }
    }
    
    public void divide(int ndivisions){
        
    }
    /**
     * returns a random generated values from the cell 
     * @param values returned random numbers
     */
    public void getRandom(double[] values){
        getRandomFromCell(mcCells.get(0), mcFunction, values);        
    }
    
    public void showStats(){
        double efficiency = ((double) mcGenerationMisses)/mcGenerationTotal;
        System.out.println(String.format(" SEEDS = %12d , MISSES = %12d, EFFICIENCY = %.3f",
                this.mcGenerationTotal,this.mcGenerationMisses, efficiency));
    }
    /**
     * generate function from the cell.
     * @param cell
     * @param func
     * @param values 
     */
    public void getRandomFromCell(MCell cell, IMCFunc func, double[] values){    
        boolean   passed = false;
        double    weight = 0.0;
        double  fraction = 0.0;
        int     infLoopCounter = 0;
        while(passed==false){
            mcGenerationTotal++;
            cell.random(values);
            weight = func.getWeight(values);
            fraction = weight/cell.getWeight();
            double sample = Math.random();
            if(sample<fraction){
                passed = true;
            } else {
                mcGenerationMisses++;
            }
            infLoopCounter++;
            if(infLoopCounter>2100000){
                passed = true;
                System.out.println("*** warning *** could not generate a events in the cell");
            }
        }
    }
    
    public void init(){
        for(int i = 0; i < this.mcCells.size(); i++){
            this.exploreCellWeight(mcCells.get(i), mcFunction, 10000);
            System.out.println(" EXPLORATION OF CELL " + i);
            System.out.println(mcCells.get(i));
        }
    }
    
    public static void main(String[] args){
        CosineFunction2D cosFunc = new CosineFunction2D();
        MCFoam foam = new MCFoam(cosFunc);
        foam.init();
        double[] values = new double[2];
        for(int i = 0; i < 50000; i++){
            foam.getRandom(values);
            System.out.println(ArrayUtils.getString(values,"%.5f", " "));
            //System.out.println(values);
        }
        foam.showStats();
    }
}
