/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.util.List;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.ParticleList;
import org.jlab.jnp.physics.PhysicsEvent;
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
        for(String inputFile : input){
            LundReader reader = new LundReader();
            reader.addFile(inputFile);
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
        
        for(String inputFile : input){
            ReadClas12Events reader = new ReadClas12Events(inputFile);
            
            reader.setDataFilter(datafilter);
            reader.setMcFilter(mcfilter);
            while(reader.readNext()==true){
                counter++;
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
                    write_counter++;
                }
            }
            System.out.println("\n\n*** WRITE STATUS *** Processed : " + counter + 
                    " , Writes : " + write_counter +
                    " , Data Events : " + data_counter + 
                    " , MC Events : " + mc_counter + "\n\n");
        }
        writer.close();
        
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
    }
}
