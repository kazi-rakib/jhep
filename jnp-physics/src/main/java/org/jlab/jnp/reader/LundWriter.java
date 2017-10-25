/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class LundWriter {
    
    private int     maximumEvents = 10000;
    private String   nameTemplate = "";
    private String      directory = "";
    private String      extension = ".lund";
    
    private int       currentFile = 0;
    private BufferedWriter writer = null;
    private int    numberOfEventsWritten = 0;
//new BufferedWriter(new FileWriter(logFile));
    
    public LundWriter(String filename, int maxEvents){
        nameTemplate  = filename;
        maximumEvents = maxEvents;
        this.openFile();
    }
    
    public LundWriter setDirectory(String dir){
        directory = dir; return this;
    }
    
    public LundWriter setNameTemplate(String template){
        this.nameTemplate = template; return this;
    }
    
    public LundWriter setMaximumLines(int max){
        this.maximumEvents = max; return this;
    }
    
    public LundWriter setExtension(String ext){
        this.extension = ext; return this;
    }
    
    private void openFile(){
        
        String trailer = this.getNumberedName(currentFile);
        StringBuilder str = new StringBuilder();
        str.append(nameTemplate).append("_").append(trailer);
        str.append(extension);
        currentFile++;
        System.out.println("[FILE] ---> openning file : " + str.toString());
        if(writer!=null){
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(LundWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File file = new File(str.toString());
        try {
            FileWriter fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
        } catch (IOException ex) {
            Logger.getLogger(LundWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void writeEvent(String eventLundString){
       if(numberOfEventsWritten>=maximumEvents){
           openFile();
           numberOfEventsWritten = 0;
       }
       
        try {
            writer.write(eventLundString);
            numberOfEventsWritten++;
        } catch (IOException ex) {
            Logger.getLogger(LundWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void close(){
        if(this.writer!=null){
            
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(LundWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private String getNumberedName(int number){
        StringBuilder str = new StringBuilder();
        String ns = Integer.toString(number);
        int leading_zeros = 8 - ns.length();
        for(int i = 0; i < leading_zeros; i++) str.append('0');
        str.append(ns);
        return str.toString();
    }
    
    public static void main(String[] args){
        LundWriter writer = new LundWriter("testing",20);
        for(int i = 0; i < 120; i++){
            String events = String.format("PORTO - %d\n", i);
            writer.writeEvent(events);
        }
        writer.close();
    }
}
