/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.matrix;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class MatrixSelection {
    
    List<DimensionSelector>  selectors = new ArrayList<DimensionSelector>();
    List<String>               columns = new ArrayList<String>();
    
    private int currentColumn =  0;    
    private int    currentBin =  0;
    
    public MatrixSelection(int[] nbins){
        for(int i = 0; i < nbins.length; i++){
            selectors.add(new DimensionSelector(nbins[i]));
        }
    }
    
    public void setRange(int dim, int bmin, int bmax){
        this.selectors.get(dim).setRange(bmin, bmax);
    }
    
    public void setColumns(List<String> c){
        this.columns.clear();
        for(String s : c){ columns.add(s);}
    }
    
    public List<String> getColumns(){return columns;}
    
    public void setBin(int dim){
        currentBin = dim;
    }
    
    public int getBin(){
        return this.currentBin;
    }
    
    public void setColumn(int c){
        currentColumn = c;
    }
    
    public void setColumn(String name){
        currentColumn = 0;
        for(int i = 0; i < this.columns.size(); i++){
            //System.out.println(" compare [" + name +"] with ["+columns.get(i) + " = " + name.compareTo(columns.get(i)));
            if(name.compareTo(columns.get(i))==0){ currentColumn = i;}
            //System.out.println();
        }
    }
    
    public int getColumn(){
        return currentColumn;
    }
    
    public void reset(){
        this.currentBin = 0;
        this.currentColumn = 0;
        for(DimensionSelector d : this.selectors){
            d.setRange(d.getMin(), d.getMax());
        }
    }
    
    public boolean isValid(int[] bins){
        for(int i = 0; i < selectors.size();i++){
            if(i!=currentBin){
                if(selectors.get(i).isValid(bins[i])==false) return false;
            }
        }
        return true;
    }
    
    public int getSize(){return this.selectors.size();}
    public DimensionSelector getSelector(int index){return this.selectors.get(index);}

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("--- BIN %d --- COLUMN %d ---\n", currentBin,currentColumn));
        str.append("--");
        for(int i = 0; i < this.getSize(); i++){
            str.append(String.format("%3d : %s\n", i,this.selectors.get(i).toString()));
        }
        return str.toString();
    }
    
    public static class DimensionSelector {
        
        private int min;
        private int max;
        private int binMin;
        private int binMax;
        
        public DimensionSelector(int __min, int __max){
            min = __min;
            max = __max;
            
            binMin = -1; binMax = -1;
        }
        
        public DimensionSelector(int nbins){
            min = 0;
            max = nbins - 1;
            binMin = 0; binMax = max;
        }
        
        public DimensionSelector setRange(int __bmin, int __bmax){
            binMin = __bmin;
            binMax = __bmax;
            return this;
        }
        
        public int getBinMin(){
            return binMin;
        }
        
        public int getBinMax(){
            return binMax;
        }
        
        public int getMin(){
            return min;
        }
        
        public int getMax(){
            return max;
        }
        public boolean isValid(int bin){
            if(binMin<0||binMax<0) return true;
            return (bin>=binMin&&min<=binMax);
        }
        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append(String.format("%8d - %8d", this.binMin,this.binMax));
            return str.toString();
        }
    }
}
