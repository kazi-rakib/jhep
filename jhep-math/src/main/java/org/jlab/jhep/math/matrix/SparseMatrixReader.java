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
import org.jlab.hep.math.data.DataAxis;
import org.jlab.hep.math.data.DataVector;
import org.jlab.jhep.readers.TextFileReader;
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
    
    public SparseMatrix readTextMatrix(String filename) {
        //List<String> headerLines = FileUtils.readFile(filename, "#!", true);
        String jsonString = FileUtils.readFileAsString(filename, "#!", true);
         Reader reader;
         System.out.println(jsonString);
        try {
            reader = new FileReader(filename);
            JsonObject  object = Json.parse(jsonString).asObject();
            JsonArray   axisArray = object.get("axis").asArray();
            JsonArray    vecArray = object.get("variables").asArray();
            String          model = object.get("model").asString();
            
            int nAxis = axisArray.values().size();
            int nVars =  vecArray.values().size();
            
            System.out.println("n-axis = " + nAxis);
            DataAxis[] dataAxis = new DataAxis[nAxis];
            
            for(int i = 0; i < nAxis; i++){
                String name = axisArray.get(i).asObject().get("name").asString();
                int    bins = axisArray.get(i).asObject().get("bins").asInt();
                double  min = axisArray.get(i).asObject().get("min").asDouble();
                double  max = axisArray.get(i).asObject().get("max").asDouble();
                dataAxis[i] = new DataAxis(name,bins,min,max);
            }                        
            
            SparseMatrix  matrix = new SparseMatrix();
            matrix.setName(model);
            
            matrix.initAxis(dataAxis);
            
            //List<String>  dataLines = FileUtils.readFile(filename);
            TextFileReader  textReader = new TextFileReader(filename);
            
            int[]    binIndex = new int[dataAxis.length];
            Float[]  vectorData = new Float[nVars];
            
            while(textReader.readNext()==true){
                //System.out.println(textReader.entrySize());
                //textReader.show();
                                
                for(int i = 0; i < binIndex.length; i++){
                    binIndex[i] = dataAxis[i].findBin(textReader.getAsDouble(i));
                    //System.out.println(" bin  [" + i + "] = " + binIndex[i]);
                }
                
                for(int i = 0; i < nVars; i++){
                    double value  = textReader.getAsDouble(i+nAxis);
                    vectorData[i] = (float) value; 
                }
                
                DataVector<Float> vector = new DataVector<Float>(vectorData);
                
                matrix.addData(vector, binIndex);
            }
            return matrix;
            //matrix.printContent();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SparseMatrixReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SparseMatrixReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/Software/project-1a.0.0/Distribution/jhep/jhep-math/src/main/resources/org/jlab/jhep/matrix/sample/sampleMatrix.txt";
        SparseMatrixReader reader = new SparseMatrixReader();
        SparseMatrix matrix = reader.readTextMatrix(filename);
    }
}
