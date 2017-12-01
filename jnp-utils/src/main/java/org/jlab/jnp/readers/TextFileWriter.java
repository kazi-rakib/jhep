/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.readers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class TextFileWriter {
    private BufferedWriter writer = null;
    
    public TextFileWriter(){
        
    }
    
    public void open(String filename){
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "utf-8"));
            //writer.write("Something");
        } catch (IOException ex) {
            // report
        }
    }
    
    public void writeString(String str){
        try {
            writer.write(str);
            writer.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(TextFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void close(){
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
