/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.matrix;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.math.data.DataAxis;
import org.jlab.jnp.math.data.DataVector;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.data.HipoNodeBuilder;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.hipo.io.HipoRecord;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.jnp.hipo.schema.Schema;
import org.jlab.jnp.hipo.schema.SchemaFactory;
import org.jlab.jnp.readers.TextFileReader;
import org.jlab.jnp.utils.file.FileUtils;
import org.jlab.jnp.utils.json.Json;
import org.jlab.jnp.utils.json.JsonArray;
import org.jlab.jnp.utils.json.JsonObject;

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
        //writer.setCompressionType(3);
        SchemaFactory factory  = new SchemaFactory();
        Schema         schema  = new Schema("{1200,matrix::data}[1,index,LONG][2,data,FLOAT]");        
        factory.addSchema(schema);
        HipoEvent  headerEvent = factory.getSchemaEvent();
        HipoNode   description = new HipoNode(1400,1,jsonString);
        
        headerEvent.addNode(description);
        HipoRecord record = new HipoRecord();
        record.addEvent(headerEvent.getDataBuffer());
        writer.open(hipoFile, record.build().array());
        writer.setCompressionType(3);
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
            if(nodeIndex.getSize()>50000){
                dataEvent.reset();
                dataEvent.addNode(nodeIndex.buildNode(1200, 1));
                dataEvent.addNode(nodeData.buildNode (1200, 2));
                writer.writeEvent(dataEvent);
                nodeIndex.reset();
                nodeData.reset();
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
            String  type = axisArray.get(i).asObject().get("type").asString();
            dataAxis[i] = new DataAxis(name,bins,min,max);
            if(type.compareTo("log")==0) dataAxis[i].setLog(true);
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
            //System.out.println(" INDEX NODE SIZE = " + indexNode.getDataSize() + "  VECTOR SIZE = " + vectorNode.getDataSize());
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
                String  type = axisArray.get(i).asObject().get("type").asString();

                dataAxis[i] = new DataAxis(name,bins,min,max);
                if(type.compareTo("log")==0) dataAxis[i].setLog(true);
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
                   // binIndex[i] = dataAxis[i].findBin(textReader.getAsDouble(i));
                    //System.out.println(" bin  [" + i + "] = " + binIndex[i]);
                    binIndex[i] = textReader.getAsInt(i);
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
    
    
    public static void writeSparseMatrix(SparseMatrix matrix, String filename){
        HipoWriter writer = new HipoWriter();
        //writer.setCompressionType(3);
        SchemaFactory factory  = new SchemaFactory();
        Schema         schema  = new Schema("{1200,matrix::data}[1,index,LONG][2,data,FLOAT]");
        Schema         schemaAxis = new Schema("{1201,matrix::axis}[1,names,STRING][2,types,STRING][3,min,FLOAT][4,max,FLOAT][5,bins,INT]");
        
        factory.addSchema(schema);
        factory.addSchema(schemaAxis);
        
        HipoEvent  headerEvent = factory.getSchemaEvent();
        //HipoNode   description = new HipoNode(1400,1,jsonString);
        HipoNode vectorNames = new HipoNode(1201,1,matrix.getVectorNames());
        HipoNode axisNames = new HipoNode(1201,2,matrix.getAxisNames());
        HipoNode axisTypes = new HipoNode(1201,3,matrix.getAxisTypes());
        HipoNode axisMins  = new HipoNode(1201,4,matrix.getAxisMins());
        HipoNode axisMaxs  = new HipoNode(1201,5,matrix.getAxisMaxs());
        HipoNode axisBins  = new HipoNode(1201,6,matrix.getAxisBins());
        
        headerEvent.addNodes(Arrays.asList(vectorNames,axisNames,axisTypes,axisMins,axisMaxs,axisBins));
        
        HipoRecord record = new HipoRecord();
        record.addEvent(headerEvent.getDataBuffer());
        
        writer.open(filename, record.build().array());
        writer.setCompressionType(3);
        writer.setCompression(true);
        
        Map<Long,DataVector<Float>> map = matrix.getMatrixMap();
        HipoNodeBuilder<Long>  nodeIndex = new HipoNodeBuilder<Long>();
        HipoNodeBuilder<Float>  nodeData = new HipoNodeBuilder<Float>();
        HipoEvent              dataEvent = new HipoEvent();
        
        int vectorSize = matrix.getVectorSize();
        System.out.println("SIZE = " + vectorSize);
        for(Map.Entry<Long,DataVector<Float>> entry : map.entrySet()){
            //System.out.printf("%14d : %12.5f\n",entry.getKey(),entry.getValue().valueOf(0));
            nodeIndex.push(entry.getKey());
            for(int i = 0; i < vectorSize; i++){
                nodeData.push(entry.getValue().valueOf(i));
            }
            if(nodeIndex.getSize()>50000){
                System.out.println("-----> writing output....");
                dataEvent.reset();
                dataEvent.addNode(nodeIndex.buildNode(1200, 1));
                dataEvent.addNode(nodeData.buildNode (1200, 2));
                writer.writeEvent(dataEvent);
                nodeIndex.reset();
                nodeData.reset();
            }
            //System.out.println(nodeIndex.getSize());
        }
        
        dataEvent.reset();
        dataEvent.addNode(nodeIndex.buildNode(1200, 1));
        dataEvent.addNode(nodeData.buildNode (1200, 2));
        
        writer.writeEvent(dataEvent);
        
        writer.close();
    }
    
    
    public static SparseMatrix readSparseMatrix(String filename){
        HipoReader reader = new HipoReader();
        reader.open(filename);
        
        //SparseMatrix  matrix = new SparseMatrix();
        HipoRecord headerRecord = reader.getHeaderRecord();
        
        HipoEvent headerEvent = new HipoEvent(headerRecord.getEvent(0));
        
        headerEvent.showNodes();
        HipoNode vectorNames = headerEvent.getNode(1201, 1);
        HipoNode axisNames = headerEvent.getNode(1201, 2);
        HipoNode axisTypes = headerEvent.getNode(1201, 3);
        HipoNode axisMins  = headerEvent.getNode(1201, 4);
        HipoNode axisMaxs  = headerEvent.getNode(1201, 5);
        HipoNode axisBins  = headerEvent.getNode(1201, 6);
        
        String[] tokens_an = axisNames.getString().split(":");
        String[] tokens_at = axisTypes.getString().split(":");
        String[] tokens_vn = vectorNames.getString().split(":");
        
        //System.out.println("LENGTH = " + tokens.length);
        DataAxis[] axis = new DataAxis[tokens_an.length];
        for(int i = 0; i < axis.length; i++){
            axis[i] = new DataAxis(tokens_an[i],axisBins.getInt(i),axisMins.getDouble(i),axisMaxs.getDouble(i));
            if(tokens_at[i].equals("log")) axis[i].setLog(true);
            System.out.println(axis[i].toString());
        }
        
        //System.out.println(" DATA AXIS SIZE = " + axis.length);
        SparseMatrix matrix = new SparseMatrix(tokens_vn);
        matrix.initAxis(axis);
        
        int nrecords = reader.getRecordCount();
        int nvectors = matrix.getVectorSize();
        System.out.println("[Matrix] N-RECORDS = " + nrecords + "  VECTOR SIZE = " + nvectors + "  " + tokens_vn.length);
        for(int i = 0; i < tokens_vn.length; i++) System.out.println("[" + tokens_vn[i] + "]");
        for(int r = 0; r < nrecords; r++){
            HipoRecord record = reader.readRecord(r);
            int nevents = record.getEventCount();
            for( int e = 0; e < nevents; e++){
                
                byte[] array = record.getEvent(e);
                HipoEvent event = new HipoEvent(array);
                
                HipoNode nodeIndex  = event.getNode(1200, 1);
                HipoNode nodeVector = event.getNode(1200, 2);
                //System.out.println(" INDEX = " + nodeIndex.getDataSize() + "  VECTOR = " + nodeVector.getDataSize());
                for(int n = 0; n < nodeIndex.getDataSize(); n++){
                    int vi = n*nvectors;
                    DataVector<Float> vec = new DataVector<Float>(nvectors,0.0f);
                    
                    for(int v = 0; v < nvectors; v++) vec.setValue(v, nodeVector.getFloat(vi+v));
                    
                    matrix.addData(nodeIndex.getLong(n), vec );
                }
            }
        }
        
        //matrix.show();
        //matrix.printContent();
        return matrix;
    }
    
    public static void main(String[] args){
        //String filename = "/Users/gavalian/Work/Software/project-1a.0.0/Distribution/jhep/jhep-math/src/main/resources/org/jlab/jhep/matrix/sample/sampleMatrix.txt";
        //SparseMatrixReader reader = new SparseMatrixReader();
        //SparseMatrix matrix = reader.readTextMatrix(filename);
        //SparseMatrixReader.exportMatrix(filename, "test_matrix.hipo");
        SparseMatrix  matrix = new SparseMatrix(new String[]{"cross","acceptance"});
        DataAxis  axisX = new DataAxis("a",25,0.0,1.0);
        DataAxis  axisY = new DataAxis("b",35,0.0,1.0);
        DataAxis  axisZ = new DataAxis("c",5,0.0,1.0);
        matrix.initAxis(axisX,axisY,axisZ);
        
        
        Map<String,Double>  items = new HashMap<String,Double>();
        for(int i = 0; i < 6400; i++){
            double a = Math.random();
            double b = Math.random();
            double c = Math.random();
            double w = Math.random();
            
            items.clear();
            items.put("a", a); items.put("b", b); items.put("c", c);
            matrix.fill(0, items, 1.0);
            matrix.fill(1, items, w);
        }
        
        matrix.show();
        
        matrix.printContent();
        SparseMatrixReader.writeSparseMatrix(matrix, "text_matrix.hipo");
        
        SparseMatrix   matrixR = SparseMatrixReader.readSparseMatrix("text_matrix.hipo");
        SparseMatrix matrixInt = MatrixOperations.integrate(matrixR, 0);
        matrixInt.printContent();
        System.out.println(matrixR.contentString());
        SparseMatrix matrix1D = MatrixOperations.integrate(matrixInt, 0);
        System.out.println(matrix1D.contentString());
    }
}
