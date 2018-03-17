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

    private      Reader reader = null;
    private  boolean forceScan = true;
    private  final SchemaFactory  schemaFactory = new SchemaFactory();
    
    public HipoReader(){
        //reader = new Reader();
    }
    
    public HipoReader(boolean fs){
        forceScan = fs;
        //reader = new Reader();
    }
    /**
     * Returns a record that is written in the header of the file.
     * This is before the first record in the file. It is provided
     * by the user when opening a file for writing.
     * @return input stream containing user header record.
     */
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
    /**
     * Open a file for reading. This method uses force scan, it reads through
     * entire file and makes index of all records. Constructs full map of events
     * inside of the file for random access.
     * @param filename filename in HIPO format to open
     */
    public void open(String filename){
        long start_time = System.currentTimeMillis();        
        reader = new Reader(filename,forceScan);
        long end_time   = System.currentTimeMillis();
        //System.out.println(" HAS FIRST EVENT = " + reader.hasFirstEvent() + "  dictionary " + reader.hasDictionary());
        ByteBuffer userHeader = reader.readUserHeader();
        //System.out.println("[dictionary::open]  FIRST EVENT SIZE = " + userHeader.capacity());
        //System.out.println("[Reader::Open] ---> opened a reader with event count " 
        //        + reader.getEventCount());
        //System.out.println("[Reader::Open] ---> number of records = " + reader.getRecordCount());
        if(userHeader.capacity()>56){
            //System.out.println("[READER] ---> initializing dictionary........");
            RecordInputStream userRecord = new RecordInputStream();
            try {
                userRecord.readRecord(userHeader, 0);
                //System.out.println("[READER] ---> user header record event count " + userRecord.getEntries());
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
                //System.out.println("[READER] ---> total number of schema loaded " + schemaFactory.getSchemaList().size());
            } catch (HipoException ex) {
                Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        long openTime = end_time - start_time;
         String outMessage = String.format("** reader-open ** records = %d, events = %d, schemas = %d, time = %d ms",
                    reader.getRecordCount(), reader.getEventCount(), 
                    schemaFactory.getSchemaList().size(), openTime);
         System.out.println(outMessage);
    }
    
    /**
     * Returns number of records in the file.
     * @return 
     */
    public int getRecordCount(){
        return reader.getRecordCount();
    }
    /**
     * Reads the record with given index into the internal RecordInputStream.
     * The global position in of last read event is not modified, so if getNextEvent()
     * method is called the reader will reload the record that contains the event that
     * is next to previously read event. 
     * @param index record index in the file
     * @return ture if successful or false if the read fails.
     */
    public boolean readRecord(int index){
        try {
            return reader.readRecord(index);
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    /**
     * returns Schema factory that was initialized from the file.
     * @return schema for the file describing all bank structures
     */
    public SchemaFactory getSchemaFactory(){
        return this.schemaFactory;
    }
    /**
     * returns number of events in the file (for all the records).
     * @return number of events
     */
    public int getEventCount(){
        if(reader==null) return 0;
        return reader.getEventCount();
    }
    /**
     * Checks to see if there are any events left in the file to be read.
     * The position of last read event is stored in the reader.
     * @return true if there are more events to read with getNextEvent().
     */
    public boolean hasNext(){
        if(reader==null) return false;
        return reader.hasNext();
    }
    /**
     * Returns the previous event in the event stream, the position is moved
     * backwards, and when getNextEvent() is called, you'll get the event that
     * was read before getPreviousEvent() call.
     * @return HipoEvent with the dictionary from the file.
     */
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
    /**
     * Reads the next event in the event stream, the position in the stream is
     * incremented. Use hasEvent() method to check if there are events available
     * before calling this method.
     * @return HipoEvent with the dictionary from the file.
     */
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
    /**
     * returns number of events inside the currently loaded record.
     * NOTE: this is not the number of events in the file.
     * @return number of events
     */
    public int getRecordEventCount(){
        return reader.getCurrentRecordStream().getEntries();
    }
    /**
     * Reads an event with given index, the index is the absolute index of the 
     * event in the file. To read relative event to the record, use 
     * readRecordEvent(index) method. To find out number of events in the record
     * use getRecordEventCount() method.
     * @param index index of the event in the file
     * @return HIPO event constructed from the index-th buffer.
     */
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
    /**
     * Reads an event with relative offset of index from the currently loaded
     * record. To use this method, first use 
     * readRecord(int rec);
     * int count = getRecordEventCount();
     * and the index variable has to be between 0-count.
     * @param index
     * @return HipoEvent with dictionary from the file
     */
    public HipoEvent readRecordEvent(int index){
        byte[] event;        
        event = reader.getCurrentRecordStream().getEvent(index);
        return new HipoEvent(event,schemaFactory);
    }
    
    public void readRecordEvent(DataEventHipo event, int index){
        try {
            int dataSize = reader.getCurrentRecordStream().getEventLength(index);
            if(dataSize<0) {
                System.out.println(" ** error ** failed to read event # " + index);
                return;
            }
            event.resize(dataSize);            
            reader.getCurrentRecordStream().getEvent(event.eventBuffer, 0, index);            
            event.eventBuffer.putInt(event.EVENT_LENGTH_WORD_POSITION, dataSize);
            event.setSchemaFactory(schemaFactory);
            event.updateIndex();
        
        } catch (HipoException ex) {
            Logger.getLogger(HipoReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*  public int getRecordCount(){
        return reader.getRecordCount();
    }*/
    
    /**
     * Closes the file and disposes the reader object.
     */
    public void close(){
       reader = null; 
    }
    
    public static void main(String[] args){
        String file = "/Users/gavalian/Work/Software/project-3a.0.0/Distribution/jnp/jnp-hipo/clasrun_2475.hipo.0";
        HipoReader reader = new HipoReader();
        reader.open(file);
        //reader.open("/Users/gavalian/Work/Software/project-3a.0.0/Distribution/clas12-offline-software/coatjava/clas_000810_324.hipo");
        //+ "/Users/gavalian/Work/Software/project-3a.0.0/Distribution/clas12-offline-software/coatjava/clas_000810_324_v5_c2.hipo");
        int nevents = reader.getEventCount();
        System.out.println(" N# = " + nevents);
        //        HipoEvent event = reader.readEvent(1);      
        //for(int i = 0; i < nevents; i++){
        int icounter = 0;
         //while(reader.hasNext()==true){
         
         DataEventHipo dataEvent = new DataEventHipo();
         
         for(int i = 0; i < 1500; i++){
             HipoEvent event = reader.readNextEvent();
             ByteBuffer buff = event.getDataByteBuffer();
             dataEvent.init(buff, 0, buff.capacity());
             
             //dataEvent.show();
             int size = dataEvent.getSize(dataEvent.getHash(331,1));
             System.out.print(" PID    : ");
             for(int b = 0; b < size; b++){
                 System.out.print(String.format("%8d", dataEvent.getInt(dataEvent.getHash(331,1), b)));

             }
             System.out.println(" ");
             System.out.print(" CHARGE : ");
             for(int b = 0; b < size; b++){
                 System.out.print(String.format("%8d", dataEvent.getInt(dataEvent.getHash(331,8), b)));
             }
             System.out.println(" ");
             System.out.print(" BETA   : ");
             for(int b = 0; b < size; b++){
                 System.out.print(String.format("%8.4f", dataEvent.getFloat(dataEvent.getHash(331,9), b)));
             }
             System.out.println(" ");
             //System.out.println(" reading event " + icounter);
             icounter++;
        }
        //reader.open("/Users/gavalian/Work/Software/project-3a.0.0/Distribution/clas12-offline-software/coatjava/clas_000810_324_v5_c2.hipo");
    }
}
