/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.detector;

import java.util.HashMap;
import java.util.Map;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.tree.Tree;
import org.jlab.groot.tree.TreeFile;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.data.HipoNodeType;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.EventSelector;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.ParticleSelector;

/**
 *
 * @author gavalian
 */
public class EventSelectors extends Tree {
    
    private Map<String,EventSelector> eventSelectors = new HashMap<String,EventSelector>();
    private HipoReader                   eventReader = new HipoReader();
    private DetectorEvent                detectorEvent = new DetectorEvent();
    private GenericFitter                fitter = new GenericFitter();
    private EventFilter                  filter = new EventFilter();
    private EventFilter                  filterMC = new EventFilter("X+:X-:Xn");
    
    public EventSelectors(){
        super("PHYSICS");
    }
    
    public void addSelector(String name, String format){
        /*ParticleSelector selector = new ParticleSelector();
        selector.parse(format);
        */
        eventSelectors.put(name, new EventSelector(format));
        updateBranches();
    }
    
    
    public void addSelector(String name, String format, String property){
        /*ParticleSelector selector = new ParticleSelector();
        selector.parse(format);
        */
        EventSelector es = new EventSelector(format);
        es.setProperty(property);
        eventSelectors.put(name, es);
        updateBranches();
    }
    
    private void updateBranches(){
        this.getBranches().clear();
        for(Map.Entry<String,EventSelector> item : this.eventSelectors.entrySet()){
            this.addBranch(item.getKey(), "", "");
        }
    }
    
    public void setFile(String inputFile){
        eventReader.open(inputFile);
    }
    
    public void setEnergy(double energy){
        this.fitter.setEnergy(energy);
    }
    
    public void setFilter(String options){
        this.filter.setFilter(options);
    }
    
    public void setMCFilter(String options){
        this.filterMC.setFilter(options);
    }
    
    @Override
    public int getEntries(){
        return eventReader.getEventCount();
    }
    
    @Override
    public int readEntry(int entry){
        
        HipoEvent event = eventReader.readEvent(entry);
        fitter.readEvent(event, detectorEvent.getPhysicsEvent());
        
        //System.out.println(" Entry read = " + entry);
        //System.out.println(detectorEvent.getPhysicsEvent().toLundString());
        if(filter.isValid(detectorEvent.getPhysicsEvent())==true&&
                filterMC.checkFinalState(detectorEvent.getPhysicsEvent().getGeneratedParticleList())==true){
            
            float[] data = new float[this.eventSelectors.size()];
            int counter = 0;
            for(Map.Entry<String,EventSelector> item : this.eventSelectors.entrySet()){
                //Particle p = item.getValue().get(detectorEvent.getPhysicsEvent());
                Double value = item.getValue().getValue(detectorEvent.getPhysicsEvent());
                if(Double.isInfinite(value)){
                    System.out.println(" Vooops " + value);
                    System.out.println(detectorEvent.getPhysicsEvent().toLundString());
                }
               data[counter] = (float) item.getValue().getValue(detectorEvent.getPhysicsEvent());
               //System.out.println("mass 2 = " + p.mass2());
               counter++;
            }
            //for(int i = 0; i < data.length; i++) System.out.print(" " + data[i]);
            //System.out.println();
            this.setBranchData(data);
            return 1;
        }
        //System.out.println(detectorEvent.getPhysicsEvent().toLundString());
        return -1;
    }
    
    public static void main(String[] args){
        String inputFile = "/Users/gavalian/Work/Software/project-3a.0.0/Distribution/out_clas_002916_FILTERED_67.hipo";
        EventSelectors selector = new EventSelectors();
        
        selector.setFile(inputFile);
        selector.setFilter("11:22:22:X+:X-:Xn");
        selector.setEnergy(11.0);
        
        //selector.addSelector("w2", "[b]+[t]-[11]","mass2");
        //selector.addSelector("pi0m", "[22,0]+[22,1]","mass2");
        selector.addSelector("epr","(11)","p");
        selector.addSelector("epg","[11]","p");
        
        EventSelector gep = new EventSelector();
        
        DataVector vec = selector.getDataVector("epr", "");
        System.out.println(" QUERY : Entries = " + vec.getSize() + " , Min = " + vec.getMin()
                + " , Max = " + vec.getMax());
        //DataVector vec = selector.getDataVector("pi0m", "");
        //System.out.println(" VECTOR SIZE = " + vec.getSize());
        /*for(int i = 0; i < 2000; i++){
          int status = selector.readEntry(i);
          if(status>0){
              //System.out.println( i + "  found an event");
          }
        }*/
    }
}
