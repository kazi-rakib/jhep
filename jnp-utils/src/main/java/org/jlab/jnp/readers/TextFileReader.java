/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class TextFileReader {
    
    BufferedReader br = null;
    
    List<String>  entryTokens = new ArrayList<String>();
    String        tokenizerSymbol = "\\s+";
    List<String>      ignoreLines = new ArrayList<>();
    
    public TextFileReader(){
        ignoreLines.add("#");
    }
        
    public TextFileReader(String filename){
        ignoreLines.add("#");
        open(filename);
    }
    
    public void setSeparator(String delim){
        this.tokenizerSymbol = delim;
    }
    
    public final void open(String filename){
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(TextFileReader.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[TextFileReader] --> error : failed to open file : " + filename);
            br = null;
        }
    }
    
    private boolean checkLine(String line){
        for(String ignore : this.ignoreLines){
            if(line.startsWith(ignore)==true) return false;
        }
        return true;
    }
    
    public boolean readNext(){
        
        entryTokens.clear();
        if(br==null){
            return false;
        }
        
        try {
    
            String line = br.readLine();
            while(line!=null&&this.checkLine(line)==false){
                line = br.readLine();
            }
            if(line==null){
                br = null;
                return false;
            }
            
            String[] tokens = line.trim().split(tokenizerSymbol);
            for(String token : tokens){
                entryTokens.add(token);
            }
        } catch (IOException ex) {
            //Logger.getLogger(TextFileReader.class.getName()).log(Level.SEVERE, null, ex);
            br = null;
            return false;
        }
        return true;
    }
    
    public short[]   getAsShortArray(){
        short[] array = new short[entryTokens.size()];
        for(int i =0; i < array.length; i++){
            try {
                array[i] = Short.parseShort(entryTokens.get(i).trim());
            } catch(Exception e){
                System.out.println("[TextReader] ** error ** can not convert "
                        + entryTokens.get(i) + " to short");
                array[i] = 0;
            }
        }
        return array;
    }
    
    public double[] getAsDoubleArray(){
        double[] array = new double[entryTokens.size()];
        for(int i =0; i < array.length; i++){
            try {
                array[i] = Double.parseDouble(entryTokens.get(i));
            } catch(Exception e){
                System.out.println("[TextReader] ** error ** can not convert "
                + entryTokens.get(i) + " to integer");
                array[i] = 0;
            }
        }
        return array;
    }
    
    public double[] getAsDoubleArray(int start, int length){
        double[] array = new double[length];
        for(int i = 0; i < array.length; i++){
            array[i] = Double.parseDouble(entryTokens.get(i+start));
        }
        return array;
    }
    
    public int[]   getAsIntArray(){
        int[] array = new int[entryTokens.size()];
        for(int i =0; i < array.length; i++){
            try {
                array[i] = Integer.parseInt(entryTokens.get(i));
            } catch(Exception e){
                System.out.println("[TextReader] ** error ** can not convert "
                + entryTokens.get(i) + " to integer");
                array[i] = 0;
            }
        }
        return array;
    }
    
    public int entrySize(){ return entryTokens.size();}
    public String  getAsString( int index ){ return this.entryTokens.get(index);};
    public Integer getAsInt(    int index ){ return Integer.parseInt(this.entryTokens.get(index));};
    public Double  getAsDouble( int index ){ return Double.parseDouble(this.entryTokens.get(index));};    

    public void show(){
        for(String token : this.entryTokens){
            System.out.print(token + ":");
        }
        System.out.println();
    }
}
