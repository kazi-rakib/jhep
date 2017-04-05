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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.hep.math.data.DataAxis;
import org.jlab.hep.math.data.DataVector;
import org.jlab.jhep.hipo.data.HipoEvent;
import org.jlab.jhep.hipo.data.HipoGroup;
import org.jlab.jhep.hipo.data.HipoNode;
import org.jlab.jhep.hipo.data.HipoNodeBuilder;
import org.jlab.jhep.hipo.io.HipoReader;
import org.jlab.jhep.hipo.io.HipoRecord;
import org.jlab.jhep.hipo.io.HipoWriter;
import org.jlab.jhep.hipo.schema.Schema;
import org.jlab.jhep.hipo.schema.SchemaFactory;
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
    
    public static void exportMatrix(String textFile, String hipoFile){
        
        String jsonString = FileUtils.readFileAsString(textFile, "#!", true);
        HipoWriter writer = new HipoWriter();
        writer.setCompressionType(2);
        SchemaFactory factory  = new SchemaFactory();
        Schema         schema  = new Schema("{1200,matrix::data}[1,index,LONG][2,data,FLOAT]");        
        factory.addSchema(schema);
        HipoEvent  headerEvent = factory.getSchemaEvent();
        HipoNode   description = new HipoNode(1400,1,jsonString);
        
        headerEvent.addNode(description);
        HipoRecord record = new HipoRecord();
        record.addEvent(headerEvent.getDataBuffer());
        writer.open(hipoFile, record.build().array());
        
        SparseMatrix  matrix = SparseMatrixReader.readTextMatrix(textFile);
        
        Map<Long,DataVector<Float>> map = matrix.getMatrixMap();
        HipoNodeBuilder<Long>  nodeIndex = new HipoNodeBuilder<Long>();
        HipoNodeBuilder<Float>  nodeData = new HipoNodeBuilder<Float>();
        HipoEvent              dataEvent = new HipoEvent();
        
        int vectorSize = matrix.getVectorSize();
        
        for(Map.Entry<Long,DataVector<Float>> entry : map.entrySet()){
            //System.out.printf("%14d : %12.5f\n",entry.getKey(),entry.getValue().valueOf(0));
            nodeIndex.push(entry.getKey());
            for(int i = 0; i < vectorSize; i++){
                nodeData.push(entry.getValue().valueOf(i));
            }
        }
        
        dataEvent.reset();
        dataEvent.addNode(nodeIndex.buildNode(1200, 1));
        dataEvent.addNode(nodeData.buildNode (1200, 2));
        
        writer.writeEvent(dataEvent);
        writer.close();
        matrix.show();
    }
    
    
    public static SparseMatrix readHipoMatrix(String filename){
        HipoReader reader = new HipoReader();
        reader.open(filename);
        SparseMatrix  matrix = new SparseMatrix();
        HipoRecord headerRecord = reader.getHeaderRecord();
        System.out.println(" # EVENTS in RECORD : " + headerRecord.getEventCount());
        SchemaFactory factory = reader.getSchemaFactory();
        System.out.println("FACTORY ELEMENTS = " + factory.getSchemaList().size());
        factory.show();
        
        byte[] eventBytes = headerRecord.getEvent(0);
        HipoEvent headerEvent = new HipoEvent(eventBytes);
        HipoNode  descriptionNode = headerEvent.getNode(1400, 1);
        String    jsonString      = descriptionNode.getString();
        
        JsonObject  object = Json.parse(jsonString).asObject();
        JsonArray   axisArray = object.get("axis").asArray();
        
        
        JsonArray    vecArray = object.get("variables").asArray();
        String          model = object.get("model").asString();
        int nAxis = axisArray.values().size();
        int nVars =  vecArray.values().size();
        DataAxis[] dataAxis = new DataAxis[nAxis];
        
        for(int i = 0; i < nAxis; i++){
            String name = axisArray.get(i).asObject().get("name").asString();
            int    bins = axisArray.get(i).asObject().get("bins").asInt();
            double  min = axisArray.get(i).asObject().get("min").asDouble();
            double  max = axisArray.get(i).asObject().get("max").asDouble();
            dataAxis[i] = new DataAxis(name,bins,min,max);
        }
        matrix.setName(model);            
        matrix.initAxis(dataAxis);
        System.out.println(jsonString);
        
        int nevents = reader.getEventCount();
        Float[] values = new Float[nVars];
        
        for(int i = 0; i < nevents; i++){
            HipoEvent event = reader.readHipoEvent(i);
            HipoNode  indexNode  = event.getNode(1200, 1);
            HipoNode  vectorNode = event.getNode(1200, 2);
            System.out.println(" INDEX NODE SIZE = " + indexNode.getDataSize() + "  VECTOR SIZE = " + vectorNode.getDataSize());
            int indexSize = indexNode.getDataSize();
            for(int n = 0; n < indexSize; n++){
                Long sindex = indexNode.getLong(n);
                //DataVector<Float> vector = new DataVector<Float>();
                int counter = 0;
                for(int v = n*nVars; v < (n+1)*nVars; v++){
                    values[counter] = vectorNode.getFloat(v);
                    counter++;
                }
                DataVector<Float> vector = new DataVector<Float>(values);
                matrix.getMatrixMap().put(sindex, vector);
            }            
        }
        matrix.show();
        return matrix;
    }
    
    public static SparseMatrix readTextMatrix(String filename) {
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
        //SparseMatrix matrix = reader.readTextMatrix(filename);
        SparseMatrixReader.exportMatrix(filename, "test_matrix.hipo");
    }
}
