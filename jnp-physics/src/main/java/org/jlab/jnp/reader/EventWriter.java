/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.reader;

import java.util.List;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.data.HipoNodeType;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.jnp.hipo.schema.SchemaFactory;
import org.jlab.jnp.utils.benchmark.Benchmark;
import org.jlab.jnp.utils.options.OptionParser;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.utils.options.OptionStore;

/**
 *
 * @author gavalian
 */
public class EventWriter {
    
    HipoWriter writer = null;
    HipoEvent  hipoEvent = null;
    
    public EventWriter(String name){        
        writer = new HipoWriter();
        writer.defineSchema("mc::header", 32110, "parameters/F:weight/F");
        writer.defineSchema("mc::event" , 32111, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:mass/F:parent/B:status/B");
        writer.defineSchema("data::event", 32210, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:mass/F:beta/F:chi2pid/F:charge/B:parent/B:status/B");
        writer.defineSchema("data::detector", 32211, "id/I:pindex/S:x/F:y/F:z/F:path/F:time/F:energy/F");
        writer.setCompressionType(2);
        writer.open(name);        
        hipoEvent = new HipoEvent(writer.getSchemaFactory());
    }
    
    public void reset(){
        hipoEvent = writer.createEvent();
    }
    
    public void write(){
        writer.writeEvent(hipoEvent);
        hipoEvent.reset();
    }
    
    
    public void writeEvent(PhysicsEvent event){
        
    }
    
    public void appendMcEvent(PhysicsEvent event){
        SchemaFactory factory = hipoEvent.getSchemaFactory();
        if(factory.hasSchema("mc::event")==true){
            HipoGroup group = factory.getSchema("mc::event").createGroup(event.getParticleList().count());
            for(int i = 0; i < event.getParticleList().count();i++){
                Particle p = event.getParticleList().get(i);
                group.getNode("pid").setShort(i, (short) p.pid());
                group.getNode("px").setFloat(i, (float) p.px());
                group.getNode("py").setFloat(i, (float) p.py());
                group.getNode("pz").setFloat(i, (float) p.pz());
                group.getNode("vx").setFloat(i, (float) p.vertex().x());
                group.getNode("vy").setFloat(i, (float) p.vertex().y());
                group.getNode("vz").setFloat(i, (float) p.vertex().z());
                group.getNode("mass").setFloat(i, (float) p.vector().mass());
                group.getNode("parent").setByte(i, (byte) p.getParentParticle());
                group.getNode("status").setByte(i, (byte) p.getStatus());
            }
            hipoEvent.addNodes(group.getNodes());
        }
        
        if(factory.hasSchema("mc::header")==true){
            int nparams = event.getParameters().size();
            
            HipoNode node_p = new HipoNode(32110,1,HipoNodeType.FLOAT,nparams);
            
            HipoNode node_w = new HipoNode(32110,2,HipoNodeType.FLOAT,1);
            //System.out.println(" EVENT WEIGHT = " + event.getWeight());
            
            node_w.setFloat(0, (float) event.getWeight());
            //System.out.println(" NODE VALUE = " + node_w.getFloat(0));
            //node_p.setFloat(0, (float) event.beamParticle().e());
            //node_p.setFloat(1, 1.0f);
            for(int i = 0; i < nparams; i++) node_p.setFloat(i, (float) event.getParameters().get(i).floatValue());
            hipoEvent.addNode(node_w);
            hipoEvent.addNode(node_p);
        }
        //writer.writeEvent(hipoEvent);
    }
    
    public void appendDataEvent(PhysicsEvent event){
        SchemaFactory factory = hipoEvent.getSchemaFactory();
    
        if(factory.hasSchema("data::event")==true){
            HipoGroup group = factory.getSchema("data::event").createGroup(event.getParticleList().count());
            for(int i = 0; i < event.getParticleList().count();i++){
                Particle p = event.getParticleList().get(i);
                group.getNode("pid").setShort(i, (short) p.pid());
                group.getNode("px").setFloat(i, (float) p.px());
                group.getNode("py").setFloat(i, (float) p.py());
                group.getNode("pz").setFloat(i, (float) p.pz());
                group.getNode("vx").setFloat(i, (float) p.vertex().x());
                group.getNode("vy").setFloat(i, (float) p.vertex().y());
                group.getNode("vz").setFloat(i, (float) p.vertex().z());
                group.getNode("charge").setByte(i, (byte) p.charge());
                group.getNode("mass").setFloat(i, (float) p.vector().mass());
                group.getNode("parent").setByte(i, (byte) p.getParentParticle());
                group.getNode("status").setByte(i, (byte) p.getStatus());
            }
            hipoEvent.addNodes(group.getNodes());
        }
    }
    
    public void close(){ writer.close();}
    
    public static void convertLUND2HIPO(String outputFile, List<String> inputList){
        EventWriter writer = new EventWriter(outputFile);
        PhysicsEvent event = new PhysicsEvent();
        int icounter = 0;
        for(String item : inputList){
            LundReader  reader = new LundReader();
            reader.addFile(item);
            reader.open();
            System.out.println("[LUND] ---> opening file : " + item);
            while(reader.nextEvent(event)==true){
                //System.out.println(event.toLundString());
                writer.reset();
                //writer.writeEvent(event);
                writer.appendMcEvent(event);
                writer.write();                
                icounter++;
            }
            System.out.println("[LUND] ---> done reading file at event # " + icounter);
        }
        writer.close();
    }
    
    public static void main(String[] args){
        OptionStore parser = new OptionStore("hipoutils");
        
        parser.addCommand("-convert", "convert files to hipo format");
        //parser.getOptionParser("-convert").addRequired("-lund", "LUND file name (in ascii format)");
        parser.getOptionParser("-convert").addRequired("-o", "output file name");
       // OptionParser parser = new OptionParser("lund-convertor");
        
        //parser.addRequired("-o", "output file name");
        
        parser.parse(args);
        if(parser.getCommand().compareTo("-convert")==0){
            if(parser.getOptionParser("-convert").hasOption("-o")==true){
                String outputFile = parser.getOptionParser("-convert").getOption("-o").stringValue();
                List<String> inputList = parser.getOptionParser("-convert").getInputList();
                EventWriter.convertLUND2HIPO(outputFile, inputList);
            }
        }
    }
}
