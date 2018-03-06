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
    
    public static void processFileList(String output, List<String> input, String datafilter, String mcfilter){
        
        EventWriter  writer = new EventWriter(output);
        
        for(String inputFile : input){
            ReadClas12Events reader = new ReadClas12Events(inputFile);
            while(reader.readNext()==true){
                PhysicsEvent dataEvent = reader.readReconstructedEvent();
                PhysicsEvent mcEvent   = reader.readGeneratedEvent();
                writer.writeEvent(dataEvent, mcEvent);
            }
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
    }
}
