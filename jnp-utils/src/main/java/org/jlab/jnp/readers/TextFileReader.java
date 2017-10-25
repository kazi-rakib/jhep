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
