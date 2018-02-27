/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoEventFilter;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.jnp.hipo.schema.Schema;

import org.jlab.jnp.hipo.schema.SchemaFactory;
import org.jlab.jnp.utils.options.OptionParser;
import org.jlab.jnp.utils.options.OptionStore;

/**
 *
 * @author gavalian
 */
public class HipoUtilities {
    
    
    public static void benchmarkProcess(String filename, int mode){
        HipoReader reader = new HipoReader();
        reader.open(filename);
        int nevents  = reader.getEventCount();
        int nbanksRead = 0;
        
        long start_time = System.currentTimeMillis();
        for(int i = 0; i < nevents; i++){
            HipoEvent event = reader.readEvent(i);
            if(mode>0){
                List<String> eventGroups = event.getGroupList();
                for(String bank : eventGroups){
                    HipoGroup group = event.getGroup(bank);
                    nbanksRead++;
                }
            }
        }
        long end_time = System.currentTimeMillis();
        
        long duration = end_time - start_time;
        int  processTime = (int) (( (double) duration)/1000.0 );
        System.out.println("processed events -> " + nevents + "  time -> " + processTime + " sec");
        System.out.println("processed banks  -> " + nbanksRead);
        System.out.println(String.format("average time -> %d evt/sec",nevents/processTime));
    }
    
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
            HipoEvent event = reader.readEvent(i);
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

        System.out.printf("%36s | %12s | %12s | %8s | %10s\n","name","count",
                    "rows","freq","row freq");
        
        for(Map.Entry<String,Integer> entry : bankList.entrySet()){
            
            int nrows = bankRows.get(entry.getKey());
            int nbank = entry.getValue();
            double  rowFreq = ( (double) nrows)/nevents;
            double bankFreq = ( (double) nbank)/nevents;
            System.out.printf("%36s | %12d | %12d | %8.2f | %10.2f\n",entry.getKey(),entry.getValue(),
                    nrows,bankFreq,rowFreq);
        }
        System.out.println("\n\n");
    }
    
    public static void mergeFiles(String outputFile, List<String> inputFiles, int compression){
        HipoReader reader = new HipoReader();
        reader.open(inputFiles.get(0));
        SchemaFactory factory = reader.getSchemaFactory();
        
        HipoWriter writer = new HipoWriter();
        writer.appendSchemaFactory(factory);
        writer.open(outputFile);
        
        reader.close();
        
        for(String inFile : inputFiles){
            System.out.println("[MERGE] ---> openning file : " + inFile);
            reader.open(inFile);
            while(reader.hasNext()==true){
                HipoEvent event = reader.readNextEvent();
                writer.writeEvent(event);
            }
            reader.close();
        }
        writer.close();
    }
    
    public static void compressFile(String inputFile, String outputFile, int compression){
        HipoReader reader = new HipoReader();
        reader.open(inputFile);
        SchemaFactory  factory = reader.getSchemaFactory();
        /*
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
        writer.close();*/
    }
    
    public static void filterFile(String outputFile, List<String> inputFiles, HipoEventFilter filter){
        
        HipoWriter writer = new HipoWriter();
        writer.setCompressionType(2);
        
        HipoReader reader = new HipoReader();
        reader.open(inputFiles.get(0));
        SchemaFactory   inputFactory = reader.getSchemaFactory();
        SchemaFactory  outputFactory = filter.getSchemaFactory(inputFactory);
        
        writer.appendSchemaFactory(inputFactory);
        
        writer.open(outputFile);
        reader.close();
        for(int i = 0; i < inputFiles.size(); i++){
            System.out.println("[FILTER] ---> openning file : " + inputFiles.get(i));
            reader = new HipoReader();
            reader.open(inputFiles.get(i));
            while(reader.hasNext()==true){
                HipoEvent event = reader.readNextEvent();
                if(filter.isValid(event)==true){
                    HipoEvent outEvent = filter.getEvent(event);
                    writer.writeEvent(outEvent);
                }
            }
            reader.close();
        }
        writer.close();
    }
    
    public static void dumpFile(String filename){
        HipoReader reader = new HipoReader();
        reader.open(filename);
        while(reader.hasNext()==true){
            HipoEvent event = reader.readNextEvent();
            event.showNodes();
        }
    }
    
    public static void splitFile(String inputFile, String outputFile, Integer maxEvents){
        HipoReader reader = new HipoReader();
        reader.open(inputFile);
        SchemaFactory   inputFactory = reader.getSchemaFactory();
        
        Integer    nFile = 0;
        Integer iCounter = 0;
        String outFile = outputFile + "." + nFile.toString();
        HipoWriter writer = new HipoWriter();
        writer.setCompressionType(2);
        writer.appendSchemaFactory(inputFactory);
        writer.open(outFile);
        while(reader.hasNext()){
            HipoEvent event = reader.readNextEvent();
            writer.writeEvent(event);
            iCounter++;
            if(iCounter>=maxEvents){
                writer.close();
                nFile++;
                iCounter = 0;
                outFile = outputFile + "." + nFile.toString();
                writer = new HipoWriter();
                writer.setCompressionType(2);
                writer.appendSchemaFactory(inputFactory);
                writer.open(outFile);
                System.out.println("[SPLIT] ---> opened file : " + outFile);
            }
        }
        writer.close();
    }
    
    public static void printFileInfo(String filename){
        HipoReader reader = new HipoReader();
        reader.open(filename);
        int  nrecords = reader.getRecordCount();
        int  nevents  = reader.getEventCount();
        System.out.println(String.format("%14s : %d", "RECORDS",nrecords));
        System.out.println(String.format("%14s : %d", "EVENTS",nevents));
        SchemaFactory factory = reader.getSchemaFactory();
        if(factory!=null){
            for(Schema schema : factory.getSchemaList()){
                System.out.println(String.format("%32s : %8d : %8d ", schema.getName(),
                        schema.getGroup(), schema.getEntries()));
            }
            //factory.show();
        }
        reader.close();
    }
    
    public static void main(String[] args){
        
        OptionStore parser = new OptionStore("hipoutils");
        parser.addCommand("-filter", "filter the file for given banks");
        parser.getOptionParser("-filter").addRequired("-o", "output file name");
        parser.getOptionParser("-filter").addRequired("-e", "list of banks that should exist for event to be valid (i.e. 1234:7656:45)");
        parser.getOptionParser("-filter").addRequired("-l", "list of banks to write out (i.e. 11234:2345:65)");
        
        parser.addCommand("-info", "print information about the file");
        parser.addCommand("-stats", "print statistics about the file");
        
        parser.addCommand("-merge", "merge HIPO files");
        parser.getOptionParser("-merge").addRequired("-o", "output file name");
        parser.getOptionParser("-merge").addOption("-c", "2","compression type");

        parser.addCommand("-test", " run speed benchmark test");
        parser.getOptionParser("-test").addOption("-m", "0","speed test mode (0 - read events, 1 - read all banks)");
        
        parser.addCommand("-dump", "dump the file on the screen");
        
        parser.addCommand("-split", "split the file to smaller chanks");
        parser.getOptionParser("-split").addRequired("-n", "number of events in the file");
        parser.getOptionParser("-split").addRequired("-i", "input file name");
        parser.getOptionParser("-split").addRequired("-o", "output file pattern");
        
        parser.parse(args);
        
        
        if(parser.getCommand().compareTo("-split")==0){
            String  outputF = parser.getOptionParser("-split").getOption("-o").stringValue();
            String  inputF  = parser.getOptionParser("-split").getOption("-i").stringValue();
            Integer nEvents = parser.getOptionParser("-split").getOption("-n").intValue();
            HipoUtilities.splitFile(inputF, outputF, nEvents);
        }
        
        if(parser.getCommand().compareTo("-filter")==0){
            
            String output = parser.getOptionParser("-filter").getOption("-o").stringValue();
            List<Integer>    exBanks = parser.getOptionParser("-filter").getOption("-e").intArrayValue();
            List<Integer>   outBanks = parser.getOptionParser("-filter").getOption("-l").intArrayValue();
            List<String>  inputFiles = parser.getOptionParser("-filter").getInputList();
            
            HipoEventFilter   filter = new HipoEventFilter();
            filter.addRequired( exBanks );
            filter.addOutput(  outBanks );
            HipoUtilities.filterFile(output, inputFiles, filter);
        }
        if(parser.getCommand().compareTo("-info")==0){
             List<String>  inputFiles = parser.getOptionParser("-info").getInputList();
             HipoUtilities.printFileInfo(inputFiles.get(0));
         }
        
        if(parser.getCommand().compareTo("-stats")==0){
             List<String>  inputFiles = parser.getOptionParser("-stats").getInputList();
             HipoUtilities.processRunInfo(inputFiles.get(0));
         }
        
        if(parser.getCommand().compareTo("-merge")==0){
            List<String>  inputFiles = parser.getOptionParser("-merge").getInputList();
            String output = parser.getOptionParser("-merge").getOption("-o").stringValue();
            int    compression = parser.getOptionParser("-merge").getOption("-c").intValue();
            HipoUtilities.mergeFiles(output, inputFiles, compression);
        }
        
        if(parser.getCommand().compareTo("-dump")==0){
            List<String>  inputFiles = parser.getOptionParser("-dump").getInputList();
            HipoUtilities.dumpFile(inputFiles.get(0));
        }
        
        if(parser.getCommand().compareTo("-test")==0){
            List<String>  inputFiles = parser.getOptionParser("-test").getInputList();
            HipoUtilities.benchmarkProcess(inputFiles.get(0),parser.getOptionParser("-test").getOption("-m").intValue());
        }
        /*
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
        */
    }
}
