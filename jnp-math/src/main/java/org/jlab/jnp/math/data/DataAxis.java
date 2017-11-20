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
    
    public static DataAxis from(DataAxis axis){
        DataAxis nda = new DataAxis(axis.getName(),axis.getBins(),axis.getMin(),axis.getMax());
        nda.setLog(axis.isLog());
        return nda;
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
        if(this.log==false){
            double   step = (this.max-this.min)/this.bins;
            return   min + step*bin + step*0.5;
        }
        
        double __low  = binLowEdge(bin);
        double __high = binHighEdge(bin);
        
        return __low+0.5*(__high-__low);
    }
    /**
     * returns low edge of the bin.
     * @param bin bin number
     * @return low boundary of the bin
     */
    public final double   binLowEdge(int bin){
        if(this.log==false){
            double center = this.binCenter(bin);
            double  width = this.getBinWidth(bin);
            return (center - width*0.5);
        }
        
        double __min = Math.log10(this.min);
        double __max = Math.log10(this.max);
        double __step = (__max-__min)/this.bins;
        double __edgelog = __min+__step*bin;
        return Math.pow(10.0,__edgelog);
    }
    /**
     * returns high edge of the bin.
     * @param bin bin number
     * @return high boundary of the bin
     */
    public final double  binHighEdge(int bin){
        if(this.log==false){
            double center = this.binCenter(bin);
            double  width = this.getBinWidth(bin);
            return (center + width*0.5);
        }
        double __min = Math.log10(this.min);
        double __max = Math.log10(this.max);
        double __step = (__max-__min)/this.bins;
        double __edgelog = __min+__step*(bin+1);
        return Math.pow(10.0,__edgelog);
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
    
    public final DataAxis setLog(boolean __log)  { this.log = __log; return this; }
    
    /**
     * returns bin that given value falls into.
     * @param value
     * @return 
     */
    public int   findBin(double value){
        int bin = -1;
        for(int i = 0; i < this.bins; i++){
            if(value>=this.binLowEdge(i)&&value<this.binHighEdge(i)){
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
    
    public final void show(){
        for(int i = 0; i < this.bins; i++){
            System.out.printf("b : %4d [%e %e] center [%e]\n",i,this.binLowEdge(i),this.binHighEdge(i),
                    this.binCenter(i));
        }
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("%12s : ", name));
        str.append(String.format("bins : %5d , ", this.bins));
        str.append(String.format("min : %9.5f , ", this.min));
        str.append(String.format("max : %9.5f", this.max));        
        return str.toString();
    }
    
    public static void main(String[] args){
        DataAxis axis = new DataAxis(40,1.0,20.0);
        axis.show();
        axis.setLog(true);
        axis.show();
    }
}
