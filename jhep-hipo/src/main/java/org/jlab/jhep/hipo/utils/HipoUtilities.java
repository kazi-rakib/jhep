/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.hipo.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.jhep.hipo.data.HipoEvent;
import org.jlab.jhep.hipo.data.HipoGroup;
import org.jlab.jhep.hipo.io.HipoReader;
import org.jlab.jhep.hipo.io.HipoRecord;
import org.jlab.jhep.hipo.io.HipoWriter;
import org.jlab.jhep.hipo.schema.SchemaFactory;
import org.jlab.jhep.utils.options.OptionParser;

/**
 *
 * @author gavalian
 */
public class HipoUtilities {
    
    public static void processRunInfo(String filename){
        
        System.out.println("----> debugging : " + filename);
        HipoReader reader = new HipoReader();
        reader.open(filename);
        SchemaFactory  factory = reader.getSchemaFactory();
        int nevents  = reader.getEventCount();
        int nrecords = reader.getRecordCount();
        
        System.out.println();
        System.out.println("****************  SUMMARY : ");
        System.out.println("----> number of records : " + nrecords);
        System.out.println("----> number of  events : " + nevents);
        System.out.println("----> number of schemas : " + factory.getSchemaList().size());
        
        Map<String,Integer>  bankList = new HashMap<String,Integer>();
        Map<String,Integer>  bankRows = new HashMap<String,Integer>();
        
        for(int i = 0; i < nevents; i++){
            HipoEvent event = reader.readHipoEvent(i);
            List<HipoGroup> groups = event.getGroups();
            //System.out.println("****");
            for(HipoGroup group : groups){
                int gid = group.getSchema().getGroup();
                String name = group.getSchema().getName();
                if(bankList.containsKey(name)==false){
                    bankList.put(name, 0);
                }
                
                int ng = bankList.get(name);
                bankList.put(name, ng+1);
            
                if(bankRows.containsKey(name)==false){ bankRows.put(name, 0);}
                int nrows = bankRows.get(name);
                bankRows.put(name, nrows+group.getNodes().get(0).getDataSize());
            }
            
            
        }
        
        System.out.println(" STATISTICS: EVENT COUNT = " + nevents);
        for(Map.Entry<String,Integer> entry : bankList.entrySet()){
            
            int nrows = bankRows.get(entry.getKey());
            int nbank = entry.getValue();
            double  rowFreq = ( (double) nrows)/nevents;
            double bankFreq = ( (double) nbank)/nevents;
            System.out.printf("%24s : %12d %12d %8.2f %10.2f\n",entry.getKey(),entry.getValue(),
                    nrows,bankFreq,rowFreq);
        }
        
    }
    
    public static void compressFile(String inputFile, String outputFile, int compression){
        HipoReader reader = new HipoReader();
        reader.open(inputFile);
        SchemaFactory  factory = reader.getSchemaFactory();
        
        HipoWriter writer = new HipoWriter();
        HipoRecord schemaRecord = new HipoRecord();
        HipoEvent  schemaEvent  = factory.getSchemaEvent();
        schemaRecord.addEvent(schemaEvent.getDataBuffer());
        
        writer.open(outputFile, schemaRecord.build().array());
        writer.setCompressionType(compression);
        
        int nrecords = reader.getRecordCount();
        int nevents = reader.getEventCount();
        for(int i = 0; i < nevents; i++){
            byte[] event = reader.readEvent(i);
            writer.writeEvent(event);
        }
        writer.close();
    }
    
    public static void main(String[] args){
        OptionParser parser = new OptionParser();
        parser.addOption("-info", "0");
        parser.addOption("-verbose", "0");
        parser.addOption("-compress", "0");
        parser.parse(args);
        
        if(parser.getOption("-info").stringValue().compareTo("0")!=0){
            String filename = parser.getOption("-info").stringValue();
            HipoUtilities.processRunInfo(filename);
            return;
        }
        
        if(parser.getOption("-compress").stringValue().compareTo("0")!=0){
            String filename = parser.getOption("-info").stringValue();
            List<String> inputParams = parser.getInputList();
            int compression = parser.getOption("-compress").intValue();
            HipoUtilities.compressFile(inputParams.get(0), inputParams.get(1),compression);
            return;
        }
        parser.printUsage();
        
    }
}
