/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.coda.hipo.HipoException;
import org.jlab.coda.hipo.Reader;
import org.jlab.coda.hipo.RecordInputStream;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.schema.Schema;
import org.jlab.jnp.hipo.schema.SchemaFactory;

/**
 * HipoReader class based on combined EVIO-6/HIPO format.
 * @author gavalian
 */
public class HipoReader {
    Reader reader = null;
    private  final SchemaFactory  schemaFactory = new SchemaFactory();
    
    public HipoReader(){
        //reader = new Reader();
    }
    
    
    public RecordInputStream getUserHeaderRecord(){
        ByteBuffer userHeader = reader.readUserHeader();
        RecordInputStream userRecord = new RecordInputStream();
        try {
            userRecord.readRecord(userHeader, 0);
            return userRecord;
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void open(String filename){
        reader = new Reader(filename,true);
        //System.out.println(" HAS FIRST EVENT = " + reader.hasFirstEvent() + "  dictionary " + reader.hasDictionary());
        ByteBuffer userHeader = reader.readUserHeader();
        //System.out.println("[dictionary::open]  FIRST EVENT SIZE = " + userHeader.capacity());
        //System.out.println("[Reader::Open] ---> opened a reader with event count " 
        //        + reader.getEventCount());
        //System.out.println("[Reader::Open] ---> number of records = " + reader.getRecordCount());
        if(userHeader.capacity()>56){
            System.out.println("[READER] ---> initializing dictionary........");
            RecordInputStream userRecord = new RecordInputStream();
            try {
                userRecord.readRecord(userHeader, 0);

                System.out.println("[READER] ---> user header record event count " + userRecord.getEntries());
                for(int i =0; i < userRecord.getEntries(); i++){
                    byte[] eventBytes = userRecord.getEvent(i);
                    HipoEvent  event = new HipoEvent(eventBytes);
                    //event.showNodes();
                    if(event.hasNode(HipoWriter.SCHEMA_GROUP, HipoWriter.SCHEMA_ITEM)==true){
                        HipoNode    node = event.getNode(HipoWriter.SCHEMA_GROUP, HipoWriter.SCHEMA_ITEM);
                        //System.out.println(node.getString());
                        Schema schema = new Schema();
                        schema.setFromText(node.getString());                        
                        try {
                            schemaFactory.addSchema(schema);
                            //System.out.println(" found schema : " + schema.getName());
                            //System.out.println(schema.toString());
                            //System.out.println(schema.getText());
                        } catch (Exception ex) {
                            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                System.out.println("[READER] ---> total number of schema loaded " + schemaFactory.getSchemaList().size());
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public SchemaFactory getSchemaFactory(){
        return this.schemaFactory;
    }
    
    public int getEventCount(){
        if(reader==null) return 0;
        return reader.getEventCount();
    }
    
    public boolean hasNext(){
        if(reader==null) return false;
        return reader.hasNext();
    }
    
    public HipoEvent readPreviousEvent(){
        byte[] event;
        try {
            event = reader.getPrevEvent();
            return new HipoEvent(event,schemaFactory);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public HipoEvent readNextEvent(){
         byte[] event;
        try {
            event = reader.getNextEvent();
            return new HipoEvent(event,schemaFactory);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    
    public HipoEvent readEvent(int index){
        byte[] event;
        try {
            event = reader.getEvent(index);
            return new HipoEvent(event,schemaFactory);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int getRecordCount(){
        return reader.getRecordCount();
    }
    
    public void close(){
        
    }
    public static void main(String[] args){
        
        HipoReader reader = new HipoReader();
        reader.open("/Users/gavalian/Work/Software/project-3a.0.0/Distribution/clas12-offline-software/coatjava/clas_000810_324.hipo");
        //+ "/Users/gavalian/Work/Software/project-3a.0.0/Distribution/clas12-offline-software/coatjava/clas_000810_324_v5_c2.hipo");
        int nevents = reader.getEventCount();
        System.out.println(" N# = " + nevents);
        //        HipoEvent event = reader.readEvent(1);      
        //for(int i = 0; i < nevents; i++){
        int icounter = 0;
         while(reader.hasNext()==true){
             HipoEvent event = reader.readNextEvent();
             System.out.println(" reading event " + icounter);
             icounter++;
        }
        //reader.open("/Users/gavalian/Work/Software/project-3a.0.0/Distribution/clas12-offline-software/coatjava/clas_000810_324_v5_c2.hipo");
    }
}
