/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jlab.coda.hipo.HeaderType;
import org.jlab.coda.hipo.RecordOutputStream;
import org.jlab.coda.hipo.Writer;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.schema.Schema;
import org.jlab.jnp.hipo.schema.SchemaFactory;

/**
 * This is the version 3 of HIPO Writer, which has the same format
 * as EVIO version 6.
 * @author gavalian
 */
public class HipoWriter {
    
    private Writer writer = null;
    private  final SchemaFactory  schemaFactory = new SchemaFactory();
    /**
     * default constructor
     */
    public HipoWriter(){
        writer = new Writer(HeaderType.HIPO_FILE,ByteOrder.LITTLE_ENDIAN,10000,8*1024*1024);
        writer.setCompressionType(1);
    }
    /**
     * Adds a schema to the schema factory. by default the schema factory
     * will be appended to the user header record.
     * @param schema schema to add to the factory
     */
    public void defineSchema(Schema schema){
        schemaFactory.addSchema(schema);
    }
    /**
     * Appends the schema factory to the existing schema factory.
     * @param factory additional factory with schema
     */
    public void appendSchemaFactory(SchemaFactory factory){
        for(Schema schema: factory.getSchemaList()){
            schemaFactory.addSchema(schema);
        }
    }
    /**
     * Adds schema to schema factory, the format string will be parsed 
     * to create schema. 
     * @param name schema name
     * @param group schema group is
     * @param format schema format (example "pid/I:px/F:py/F:pz/F"}
     */
    public void defineSchema(String name, int group, String format){
        this.schemaFactory.addSchema(new Schema(name,group,format));
    }
    /**
     * Read given directory, and parse all JSON files construct Schemas
     * and append them to the internal schema factory, all these will be written
     * into the output files dictionary.
     * @param env environmental variable
     * @param relativePath path relative to environment given
     */
    public void appendSchemaFactoryFromDirectory(String env, String relativePath){
        SchemaFactory scf = new SchemaFactory();
        scf.initFromDirectory(env, relativePath);
        //scf.show();
        appendSchemaFactory(scf);
    }
    /**
     * Sets compression type for the writer's internal RecordOutputStream.
     * all the records will be compressed with this given type. The types 
     * available are:
     * 0 - no compression
     * 1 - lz4 fast compression
     * 2 - lz4 high compression
     * 3 - GZIP compression
     * @param compression 
     */
    public void setCompressionType(int compression){
        writer.setCompressionType(compression);
    }
    /**
     * returns the schema factory dictionary for the writer
     * @return dictionary of the writer
     */
    public SchemaFactory getSchemaFactory(){
        return this.schemaFactory;
    }
    
    public HipoEvent createEvent(){
        HipoEvent event = new HipoEvent(this.schemaFactory);
        return event;
    }
    /**
     * Creates a record output stream filled with the schema
     * descriptors. These can be read and initialized in the
     * reader class.
     * @return Record binary stream to write into the files
     *         user header space.
     */
    private RecordOutputStream createSchemaRecord(){
        RecordOutputStream rec = new RecordOutputStream();
        for(Schema schema : schemaFactory.getSchemaList()){
            HipoEvent event = new HipoEvent();
            HipoNode  schemaNode = schema.createNode(31111, 1);
            event.addNode(schemaNode);
            rec.addEvent(event.getDataBuffer());
        }
        return rec;
    }
    /**
     * Opens a file for writing with provided user header.
     * First checks if the file exists.
     * @param filename file name
     * @param userHeader byte[] array representing user header
     */
    public final void open(String filename, byte[] userHeader){
        if(this.outputFileExits(filename)==true){
            System.out.println("[HIPO-WRITER] ** error ** the output file already exists : " 
                    + filename);
        } else {
            writer.open(filename, userHeader);
        }
    }    
    /**
     * Opens a file for writing with provided user header.
     * First checks if the file exists.
     * @param filename file name
     */
    public final void open(String filename){        
        if(this.outputFileExits(filename)==true){
            System.out.println("[HIPO-WRITER] ** error ** the output file already exists : " 
                    + filename);
        } else {
            RecordOutputStream recDictionary = this.createSchemaRecord();
            recDictionary.getHeader().setCompressionType(2);
            //System.out.println("compression type = " + recDictionary.getHeader().getCompressionType());
            recDictionary.build();
            ByteBuffer buffer = recDictionary.getBinaryBuffer();
            int size = buffer.limit();
            int sizeWords = buffer.getInt(0);
            //System.out.println(" The encoded bytes = " + buffer.limit() + " size = " + size 
            //        + "  words = " + sizeWords);
            byte[] userHeader = new byte[sizeWords*4];
            System.arraycopy(buffer.array(), 0, userHeader, 0, userHeader.length);
            writer.open(filename,userHeader);
        }
    }
    /**
     * Check if the output file exists.
     * @param filename
     * @return 
     */
    private boolean outputFileExits(String filename){
        File f = new File(filename);
        return f.exists();
    }
    /**
     * Writes event into the file stream. Events are written 
     * into internal buffer where they are compressed when the
     * buffer reaches the maximum allowed space.
     * @param event event in HIPO format.
     */
    public void writeEvent(HipoEvent event){
        writer.addEvent(event.getDataBuffer());
    }
    /**
     * Closes the output file. The trailer will be written
     * (future plan)
     */
    public void close() {
        writer.close();
    }
    
    
    public static void main(String[] args){
        
        System.setProperty("COATJAVA", "/Users/gavalian/Work/Software/Release-4a.0.0/COATJAVA/coatjava");
        HipoWriter writer = new HipoWriter();
        writer.defineSchema("mc::event", 32100, "id/I:px/F:py/F:pz/F");
        writer.defineSchema("mc::info" , 32101, "param");
        writer.appendSchemaFactoryFromDirectory("COATJAVA", "etc/bankdefs/hipo");
        writer.open("dictionary_test.hipo");
        writer.close();
    }
}
