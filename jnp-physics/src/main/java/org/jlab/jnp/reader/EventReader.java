/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.util.List;
import java.util.Objects;
import org.jlab.jhep.hipo.data.HipoEvent;
import org.jlab.jhep.hipo.data.HipoGroup;
import org.jlab.jhep.hipo.data.HipoNode;
import org.jlab.jhep.hipo.io.HipoReader;
import org.jlab.jhep.utils.options.OptionParser;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.map.BaseMapProducer;
import org.jlab.jnp.processes.SIDIS;

/**
 *
 * @author gavalian
 */
public class EventReader {
    
    HipoReader  reader = null;
    Integer     numberOfEvents = 0;
    Integer     currentEvent   = 0;
    HipoEvent   hipoEvent      = null;
    
    public EventReader(){
        
    }
    
    public void open(String file){
        reader = new HipoReader();
        reader.open(file);
        numberOfEvents = reader.getEventCount();
        currentEvent   = 0;
    }
    
    public Boolean nextEvent(){
        if(Objects.equals(currentEvent, numberOfEvents)) return false;
        hipoEvent = reader.readHipoEvent(currentEvent);
        currentEvent++;
        return true;
    }
    
    public Boolean getMcEvent(PhysicsEvent event){
        event.clear();

        if(hipoEvent.hasGroup("mc::header")==true){
            HipoGroup header = hipoEvent.getGroup("mc::header");
            double weight = header.getNode("weight").getFloat(0);
            event.setWeight(weight);
            HipoNode nodeP = header.getNode("parameters");
            for(int i = 0; i < nodeP.getDataSize(); i++){
                event.setParameter(i, nodeP.getFloat(i));
            }
        }
        
        if(hipoEvent.hasGroup("mc::event")==false) return false;
        HipoGroup group = hipoEvent.getGroup("mc::event");
        int nrows = group.getMaxSize();
        for(int i = 0; i < nrows; i++){
            int status = group.getNode("status").getByte(i);
            int pid    = group.getNode("pid").getShort(i);
            int parent = group.getNode("parent").getByte(i);
            if(status==1){
                Particle p = new Particle(pid,
                        group.getNode("px").getFloat(i),
                        group.getNode("py").getFloat(i),
                        group.getNode("pz").getFloat(i),
                        group.getNode("vx").getFloat(i),
                        group.getNode("vy").getFloat(i),
                        group.getNode("vz").getFloat(i)
                );
                event.addParticle(p);
            }
            if(status==0&&parent==0&&pid==11){
                Particle p = new Particle(pid,
                        group.getNode("px").getFloat(i),
                        group.getNode("py").getFloat(i),
                        group.getNode("pz").getFloat(i),
                        group.getNode("vx").getFloat(i),
                        group.getNode("vy").getFloat(i),
                        group.getNode("vz").getFloat(i)
                );
                event.setBeamParticle(p);
            }
            if(status==0&&parent==0&&pid==2212){
                Particle p = new Particle(pid,
                        group.getNode("px").getFloat(i),
                        group.getNode("py").getFloat(i),
                        group.getNode("pz").getFloat(i),
                        group.getNode("vx").getFloat(i),
                        group.getNode("vy").getFloat(i),
                        group.getNode("vz").getFloat(i)
                );
                event.setTargetParticle(p);
            }
            //if()
        }
        

        return true;
    }
    
    public boolean getDataEvent(PhysicsEvent event){
        event.clear();
        if(hipoEvent.hasGroup("data::event")==false) return false;
        HipoGroup group = hipoEvent.getGroup("data::event");
        int nrows = group.getMaxSize();
        for(int i = 0; i < nrows; i++){
            int status = group.getNode("status").getByte(i);
            int pid    = group.getNode("pid").getInt(i);
            Particle p = new Particle(pid,
                        group.getNode("px").getFloat(i),
                        group.getNode("py").getFloat(i),
                        group.getNode("pz").getFloat(i),
                        group.getNode("vx").getFloat(i),
                        group.getNode("vy").getFloat(i),
                        group.getNode("vz").getFloat(i)
                );            
            event.addParticle(p);
        }
        return true;
    }
    
    public static void main(String[] args){
        
        OptionParser parser = new OptionParser();
        parser.parse(args);
        List<String> inputFiles = parser.getInputList();
        PhysicsEvent mcEvent = new PhysicsEvent();
        
        BaseMapProducer mapProducer = new BaseMapProducer();
        mapProducer.setFilter("11:X+:X-:Xn");
        
        
        mapProducer.addParticle("phi", "[321]+[-321]");
        mapProducer.addProperty("phi", "mass");
        mapProducer.addProperty("phi", "px");
        mapProducer.addProperty("phi", "py");
        
        mapProducer.addParticle("L0", "[2212]+[-211]");
        mapProducer.addProperty("L0", "mass");
        
        mapProducer.addParticle("K0", "[211]+[-211]");
        mapProducer.addProperty("K0", "mass");
        mapProducer.addParticle("L01520", "[2112]+[211]+[-211]");
        mapProducer.addProperty("L01520", "mass");
        
        SIDIS sidis = new SIDIS();
        
        for(String item : inputFiles){
            EventReader reader = new EventReader();
            reader.open(item);
            while(reader.nextEvent()==true){
                reader.getMcEvent(mcEvent);
                /*if(mapProducer.processPhysicsEvent(mcEvent)==true){
                    //System.out.println(mcEvent.toLundString());
                    //System.out.println(mapProducer.getMap().get("phi__mass"));
                    System.out.println(mapProducer.toString());
                }*/
                if(sidis.processPhysicsEvent(mcEvent)==true){
                    //System.out.println(mcEvent.toLundString());
                    //System.out.println(mapProducer.getMap().get("phi__mass"));
                    System.out.println(sidis.toString());
                }
            }
        }
    }
}
