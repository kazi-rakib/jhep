/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.util.List;
import org.jlab.jnp.hipo.abs.AbsDataMap;
import org.jlab.jnp.hipo.abs.AbsDataMapReducer;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.ParticleList;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.maps.PhysicsMapProducer;
import org.jlab.jnp.utils.benchmark.Benchmark;
import org.jlab.jnp.utils.benchmark.ProgressPrintout;
import org.jlab.jnp.utils.options.OptionStore;

/**
 *
 * @author gavalian
 */
public class ReadClas12Events {
    
    HipoReader   reader     = new HipoReader();
    HipoEvent    hipoEvent  = null;
    EventFilter  dataFilter = new EventFilter("X+:X-:Xn");
    EventFilter  mcFilter   = new EventFilter("X+:X-:Xn");
    
    public ReadClas12Events(String filename){
        reader.open(filename);
    }
    
    public void setDataFilter(String filter){
        dataFilter.setFilter(filter);
    }
    
    public void setMcFilter(String filter){
        mcFilter.setFilter(filter);
    }
    
    public EventFilter getDataFilter() { return this.dataFilter;}
    public EventFilter getMcFilter()   { return this.mcFilter;}
    
    
    
    public boolean readNext(){
        if(reader.hasNext()==true){
            hipoEvent = reader.readNextEvent();
            return true;
        }
        return false;
    }        
    
    public PhysicsEvent readReconstructedEvent(){
        ParticleList plist = EventReader.readParticleList(hipoEvent, "REC::Particle");
        if(plist.count()<1) return null;
        return EventReader.createPhysicsEvent(plist);
    }
    
    public PhysicsEvent readGeneratedEvent(){
        ParticleList plist = EventReader.readParticleList(hipoEvent, "MC::Particle");
        if(plist.count()<1) return null;
        return EventReader.createPhysicsEvent(plist);
    }
    /**
     * Imports list of LUND files, writes a HIPO file with the events that pass
     * the event filter.
     * @param output output HIPO file name
     * @param input input list of LUND text files
     * @param mcfilter filter on the events.
     */
    
    public static void importLundFiles(String output, List<String> input, String mcfilter){        
        EventWriter  writer = new EventWriter(output);        
        EventFilter filter = new EventFilter(mcfilter);
        int         nFiles = input.size();
        int     countFiles = 0;
        for(String inputFile : input){
            LundReader reader = new LundReader();
            reader.addFile(inputFile);
            countFiles++;
            System.out.println(String.format("** import ** %4d / %4d : openning file : %s", 
                    countFiles,nFiles,inputFile));
            reader.open();
            while(reader.next()){
                PhysicsEvent mcEvent = reader.getEvent();
                if(filter.isValid(mcEvent)==true){
                    writer.writeEvent(null, mcEvent);
                }
            }
        }
        writer.close();
    }
    /**
     * Processes the list of HIPO files and writes out Monte-Carlo events and
     * reconstructed events into a new HIPO file, with standard event structures.
     * @param output output HIPO file name
     * @param input list of HIPO files from CLAS12 reconstruction
     * @param datafilter filter for the reconstructed events
     * @param mcfilter filter for the generated events
     */
    public static void processFileList(String output, List<String> input, String datafilter, String mcfilter){
        
        EventWriter  writer = new EventWriter(output);
        int data_counter = 0;
        int mc_counter = 0;
        int counter    = 0;
        int write_counter = 0;
        
        ProgressPrintout progress = new ProgressPrintout();        
        for(String inputFile : input){
            ReadClas12Events reader = new ReadClas12Events(inputFile);
            
            reader.setDataFilter(datafilter);
            reader.setMcFilter(mcfilter);
            while(reader.readNext()==true){
                counter++;
                progress.updateStatus();
                PhysicsEvent dataEvent = reader.readReconstructedEvent();
                PhysicsEvent mcEvent   = reader.readGeneratedEvent();
                if(dataEvent!=null) data_counter++;
                if(mcEvent!=null) mc_counter++;
                
                boolean writeStatus = true;

                if(dataEvent!=null){
                    if(reader.getDataFilter().isValid(dataEvent)==false){
                        writeStatus = false;
                    }
                }
                
                if(mcEvent!=null){
                    if(reader.getMcFilter().isValid(mcEvent)==false){
                        writeStatus = false;
                    }
                }
                
                if(writeStatus==true){
                    writer.writeEvent(dataEvent, mcEvent);
                    //System.out.println("------ BEFORE SORTING ----");
                    //System.out.println(dataEvent.toLundString());
                    //System.out.println("------ AFTER SORTING ----");
                    dataEvent.getParticleList().sort();
                    //System.out.println(dataEvent.toLundString());
                    write_counter++;
                }
            }
            System.out.println("\n\n*** WRITE STATUS *** Processed : " + counter + 
                    " , Writes : " + write_counter +
                    " , Data Events : " + data_counter + 
                    " , MC Events : " + mc_counter + "\n\n");
        }
        writer.close();
        progress.showStatus();
        
    }
    
    public static void benchmarkPhysicsEvent(String inputFile, int mode){
        
        HipoReader reader = new HipoReader();
        reader.open(inputFile);
        int nrecords = reader.getRecordCount();
        DataEventHipo event = new DataEventHipo();
        PhysicsEvent  physEvent = new PhysicsEvent();
        PhysicsMapProducer mapProducer = new PhysicsMapProducer();
        AbsDataMap   dataMap = new AbsDataMap();
        AbsDataMapReducer reducer = new AbsDataMapReducer("mpi0>0.05&&mpi0<0.25",
                new String[] {"pi0m","w2","mpi0","mp"} );
        
        Benchmark  bench = new Benchmark();
        bench.addTimer("HIPO-READER-PHYSICS");
        
        int icount = 0;
        int icountPositive = 0;
        for(int r = 0; r < nrecords; r++){
            bench.resume("HIPO-READER-PHYSICS");
            reader.readRecord(r+1);
            int nevents = reader.getRecordEventCount();
            for(int ev = 0; ev < nevents-1; ev++){
                reader.readRecordEvent(event, ev+1);                
                
                if(mode==4){
                    EventReader.readPhysicsEvent(event, physEvent, "mc::event");
                }
                
                if(mode==5) {
                    mapProducer.createMap(event, dataMap);
                    if(dataMap.getStatus()==true){
                        icount++;
                        boolean status = reducer.reduce(dataMap);
                        if(status==true) icountPositive++;
                    }
                }
                //System.out.println(physEvent.toLundString());
                //event.show();
            }       
            bench.pause("HIPO-READER-PHYSICS");         
        }

        System.out.println(bench.toString());
        System.out.println("# of operations = " + icount + " positive " + icountPositive);
    }
    
    public static void benchmarkPhysics(String inputFile){
        HipoReader reader = new HipoReader();
        reader.open(inputFile);
        int nrecords = reader.getRecordCount();
        DataEventHipo event = new DataEventHipo();
        Benchmark  bench = new Benchmark();
        bench.addTimer("HIPO-READER");
        LorentzVector pi0 = new LorentzVector();
        LorentzVector g1 = new LorentzVector();
        LorentzVector g2 = new LorentzVector();
        
        int pid_hash = event.getHash(32111,1);
        int px_hash = event.getHash(32111,2);
        int py_hash = event.getHash(32111,3);
        int pz_hash = event.getHash(32111,4);
        int icount = 0;
        for(int r = 0; r < nrecords; r++){
            bench.resume("HIPO-READER");
            reader.readRecord(r+1);
            int nevents = reader.getRecordEventCount();
            for(int ev = 0; ev < nevents-1; ev++){
                reader.readRecordEvent(event, ev+1);
                //event.show();
                int npart = event.getSize(pid_hash);
                int index_g1 = -1;
                int index_g2 = -1;
                int gcount = 0;
                
                for(int i = 0; i < npart; i++){
                    if(event.getInt(pid_hash, i)==22){
                        gcount++;
                        if(index_g1<0){
                            index_g1 = i;
                        } else {
                            if(index_g2<0){
                                index_g2 = i;
                            }
                        }
                    }
                }
                
                if(gcount>=2){
                    g1.setPxPyPzM(
                            event.getFloat(px_hash, index_g1),
                            event.getFloat(py_hash, index_g1),
                            event.getFloat(pz_hash, index_g1),
                            0.0);
                    g2.setPxPyPzM(
                            event.getFloat(px_hash, index_g2),
                            event.getFloat(py_hash, index_g2),
                            event.getFloat(pz_hash, index_g2),
                            0.0);
                    pi0.copy(g1);
                    pi0.add(g2);
                    icount++;
                }
            }
            bench.pause("HIPO-READER");
            //System.out.println(" pions found in the sample = " + icount);
        }
        System.out.println(bench.toString());
        System.out.println(" pions found in the sample = " + icount);
    }
    public static void main(String[] args){
        
        OptionStore parser = new OptionStore("converter");
        parser.addCommand("-convert", "convert CLAS12 data to Physics Dataset");
        //parser.getOptionParser("-convert").addRequired("-lund", "LUND file name (in ascii format)");
        parser.getOptionParser("-convert").addRequired("-o", "output file name");
        parser.getOptionParser("-convert").addOption("-data", "X+:X-:Xn", "data filter");
        parser.getOptionParser("-convert").addOption("-mc", "X+:X-:Xn", "monte carlo filter");
        
        parser.addCommand("-import", "import LUND files into one HIPO file");
        parser.getOptionParser("-import").addRequired("-o", "output file name");
        parser.getOptionParser("-import").addOption("-data", "X+:X-:Xn", "data filter");
        parser.getOptionParser("-import").addOption("-mc", "X+:X-:Xn", "monte carlo filter");
        
        parser.addCommand("-test", "testing physics events class");
        parser.getOptionParser("-test").addOption("-m","0", "debug mode");
        
        parser.parse(args);
        if(parser.getCommand().compareTo("-convert")==0){
            if(parser.getOptionParser("-convert").hasOption("-o")==true){
                String outputFile = parser.getOptionParser("-convert").getOption("-o").stringValue();
                List<String> inputList = parser.getOptionParser("-convert").getInputList();
                String eventFilter = parser.getOptionParser("-convert").getOption("-data").stringValue();
                String    mcFilter = parser.getOptionParser("-convert").getOption("-mc").stringValue();
                //EventWriter.convertLUND2HIPO(outputFile, inputList);
                ReadClas12Events.processFileList(outputFile, inputList, eventFilter, mcFilter);
            }
        }
        
        if(parser.getCommand().compareTo("-import")==0){
            if(parser.getOptionParser("-import").hasOption("-o")==true){
                String outputFile = parser.getOptionParser("-import").getOption("-o").stringValue();
                List<String> inputList = parser.getOptionParser("-import").getInputList();
                String eventFilter = parser.getOptionParser("-import").getOption("-data").stringValue();
                String    mcFilter = parser.getOptionParser("-import").getOption("-mc").stringValue();
                //EventWriter.convertLUND2HIPO(outputFile, inputList);
                //ReadClas12Events.processFileList(outputFile, inputList, eventFilter, mcFilter);
                ReadClas12Events.importLundFiles(outputFile, inputList, mcFilter);
            }
        }
         if(parser.getCommand().compareTo("-test")==0){                          
             List<String> inputList = parser.getOptionParser("-test").getInputList();
             Integer      mode      = parser.getOptionParser("-test").getOption("-m").intValue();
             if(mode==0){
                 ReadClas12Events.benchmarkPhysics(inputList.get(0));
             } else {
                 for(int i = 0; i < 10; i++){
                     ReadClas12Events.benchmarkPhysicsEvent(inputList.get(0), mode);
                 }
             }
         }
        
    }
}
