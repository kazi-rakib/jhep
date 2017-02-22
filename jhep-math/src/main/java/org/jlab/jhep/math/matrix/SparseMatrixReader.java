/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.math.matrix;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jhep.utils.file.FileUtils;
import org.jlab.jhep.utils.json.Json;
import org.jlab.jhep.utils.json.JsonArray;
import org.jlab.jhep.utils.json.JsonObject;

/**
 *
 * @author gavalian
 */
public class SparseMatrixReader {
    public SparseMatrixReader(){
        
    }
    
    public void readHeader(String filename) {
        //List<String> headerLines = FileUtils.readFile(filename, "#!", true);
        String jsonString = FileUtils.readFileAsString(filename, "#!", true);
         Reader reader;
        try {
            reader = new FileReader(filename);
            JsonObject  object = Json.parse(reader).asObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SparseMatrixReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SparseMatrixReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
