/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

/**
 *
 * @author gavalian
 */
public class Parameter {
    
    private String   name = "unknown";
    private double    min = 0.0;
    private double    max = 1.0;
    private double  value = 0.5;
    
    public Parameter(){
        
    }
    
    public Parameter(String __name, double __min, double __max){
        name = __name; min = __min; max = __max;
        value = min + 0.5*(max-min);
    }
    
    public Parameter(String __name, double __value, double __min, double __max){
        name = __name; min = __min; max = __max;
        value = __value;
    }
    
    public void setName(String __name){ name = __name;}
    /**
     * set the value of the parameter, first a check is performed
     * to verify if the value is in a valid range.
     * @param __value new value to assign
     * @return parameter object
     */
    public Parameter setValue(double __value){ 
        if(__value>=min&&__value<=max){
            value = __value;
        } else {
            System.out.println("[parameter] ** error ** assigned value is out of range");
        }
        return this;
    }
    /**
     * sets the minimum for the parameter
     * @param __min
     * @return parameter object
     */
    public Parameter setMin(double __min){
        min = __min; return this;
    }
    /**
     * sets new maximum for the parameter
     * @param __max new maximum 
     * @return parameter object
     */
    public Parameter setMax(double __max){
        max = __max; return this;
    }
    
    public double getMin(){ return min;}
    public double getMax(){ return max;}
    public double getValue(){ return value;}
    
    public double getUnitValue(){
        double dist = value - min;
        return dist/(max-min);
    }
    
    public String getName(){ return name;}
    
    public double getRandom(){
        double rndm = Math.random();
        return min + (max-min)*rndm;
    }
    
    public void setRandom(){
       value = getRandom(); 
    }
    
    @Override
    public String toString(){
        return String.format("%24s : min/max (%9.5f : %9.5f) , value = %9.5f ", 
                name,min,max,value);
    }
    
    public void copyFrom(Parameter par){
        setName(par.getName());
        setMin(par.getMin());
        setMax(par.getMax());
        setValue(par.getValue());
    }
}
