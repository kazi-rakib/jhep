/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.hep.math.data;

/**
 *
 * @author gavalian
 */
public class DataAxis {
    
    private String  name = "axis";
    private Integer bins = 20;
    private Double   min = 0.0;
    private Double   max = 1.0;
    private boolean  log = false;
    
    public DataAxis(String __name, int __bins, double __min, double __max){
        this.name = __name;
        this.initAxis(__bins, __min, __max);
    }
    
    public DataAxis(int __bins, double __min, double __max){
        this.initAxis(__bins, __min, __max);
    }
    
    public final void initAxis(int __bins, double __min, double __max){
        this.bins = __bins; this.min = __min; this.max = __max;
    }
    
    
    public final double   getBinWidth(int bin){
        double binWidth = (this.max-this.min)/bins;
        return binWidth;
    }
    /**
     * returns the center of given bin.
     * @param bin bin number
     * @return central value
     */
    public final double  binCenter(int bin){
        double   step = (this.max-this.min)/this.bins;
        return   min + step*bin + step*0.5;
    }
    /**
     * returns low edge of the bin.
     * @param bin bin number
     * @return low boundary of the bin
     */
    public final double   binLowEndge(int bin){
        double center = this.binCenter(bin);
        double  width = this.getBinWidth(bin);
        return (center - width*0.5);
    }
    /**
     * returns high edge of the bin.
     * @param bin bin number
     * @return high boundary of the bin
     */
    public final double  binHighEndge(int bin){
        double center = this.binCenter(bin);
        double  width = this.getBinWidth(bin);
        return (center + width*0.5);
    }
    
    public final String   getName() { return this.name; }
    public final Integer  getBins() { return this.bins; }
    public final Double   getMin()  { return this.min;  }
    public final Double   getMax()  { return this.max;  }
    
    public final Boolean  isLog(){ return this.log;}
    
    public final DataAxis setName(String __name) { this.name = __name ; return this; }
    public final DataAxis setBins( int __bins  ) { this.bins = __bins ; return this; }
    public final DataAxis setMin( double __min ) { this.min  = __min  ; return this; }
    public final DataAxis setMax( double __max ) { this.max  = __max  ; return this; }
    
    public final DataAxis setLog(boolean __log){ this.log = __log; return this; }
    
    /**
     * returns bin that given value falls into.
     * @param value
     * @return 
     */
    public int   findBin(double value){
        int bin = -1;
        for(int i = 0; i < this.bins; i++){
            if(value>=this.binLowEndge(i)&&value<this.binHighEndge(i)){
                return i;
            }
        }
        return bin;
    }
    
    public final String asJsonString(String tag){
        StringBuilder str = new StringBuilder();
        str.append("\"").append(tag).append("\" : { ");
        str.append("\"bins\" : ").append(bins);
        str.append(", \"min\" : ").append(min);
        str.append(", \"max\" : ").append(max).append(" }");        
        return str.toString();
    }
    
}
