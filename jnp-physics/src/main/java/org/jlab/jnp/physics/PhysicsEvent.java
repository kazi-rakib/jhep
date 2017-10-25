/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.schema.SchemaFactory;
import org.jlab.jnp.utils.benchmark.Benchmark;
import org.jlab.jnp.reader.EventWriter;
import org.jlab.jnp.reader.LundReader;

/**
 *
 * @author gavalian
 */
public class PhysicsEvent {
    
    /**
     * Beam particle of the event. To get this particle through 
     * getParticle(pid) method use pid=5000.
     */
    private final Particle    beamParticle = new Particle(11,0.0,0.0,11.0);
    /**
     * Target particle for the event. To get this particle through 
     * getParticle(pid) method use pid=5001.
     */
    private final Particle  targetParticle = new Particle(2212,0.0,0.0,0.0);
    /**
     * List of the particles in the event.
     */
    private final ParticleList  eventParticleList = new ParticleList();
    /**
     * beam polarization vector. initialized in the positive Z-direction.
     */
    private final Vector3      beamPolarization = new Vector3(0.0,0.0,1.0);
    /**
     * target polarization vector. initialized in the positive Z-direction.
     */
    private final Vector3    targetPolarization = new Vector3(0.0,0.0,1.0);
    /**
     * Weight of the event. This is set from simulations, fraction of the cross 
     * section generated.
     */
    private double eventWeight = 1.0;
    
    private List<Double>  eventParameters = new ArrayList<Double>();
    
    public PhysicsEvent(){
        eventParameters.clear();
        for(int i = 0; i < 9; i++) eventParameters.add(0.0);
    }
    /**
     * adds a particle to the list of event particles.
     * @param part particle to add
     */
    public void addParticle(Particle part){
        this.eventParticleList.add(part);
    }
    
    public void setWeight(double weight){
        this.eventWeight = weight;
    }
    
    public double getWeight(){
        return this.eventWeight;
    }
    
    public void setParameter(int index, double par){
        this.eventParameters.set(index, par);
    }
    
    public void setBeamParticle(Particle bp){
        this.beamParticle.copy(bp);
    }
    
    public void setTargetParticle(Particle tp){
        this.targetParticle.copy(tp);
    }
    
    public void clear(){
        this.eventParticleList.clear();
    }
    
    public void addProperty(String name, double value){
        
    }
    public Particle beamParticle(){
        return this.beamParticle;
    }
    
    public Particle targetParticle(){
        return this.targetParticle;
    }
    
    public Particle getParticle(String operator){
        EventSelector evt_selector = new EventSelector(operator);
        return evt_selector.get(this);
        //return new Particle();
    }
    
    public Particle getParticle(int index){
        return this.eventParticleList.get(index);
    }
    
    
    public List<Double> getParameters(){
        return this.eventParameters;
    }
    
    public int getParticleIndex(int pid, int skip) {
                int skiped = 0;
                for (int loop = 0; loop < this.eventParticleList.count(); loop++) {
                        // System.err.println("searching ----> " + CLASParticles.get(loop).getPid()
                        // + " " + skip + " " + skiped);
			if (this.eventParticleList.get(loop).pid() == pid) {
                                if (skip == skiped)
                                        return loop;
                                else
                                    	skiped++;
                        }
                }
                return -1;
    }
    
    public Particle getParticleByCharge(int charge, int skip) {
        int skiped = 0;
        for (int loop = 0; loop < this.eventParticleList.count(); loop++) {
            // System.err.println("searching ----> " + CLASParticles.get(loop).getPid()
            // + " " + skip + " " + skiped);
            if (this.eventParticleList.get(loop).charge() == charge) {
                if (skip == skiped)
                    return this.eventParticleList.get(loop);
                else
                    skiped++;
            }
        }
        return null;
    }
    
    public Particle getParticleByPid(int pid, int skip) {
        if (pid == 5000)
            return this.beamParticle();
        if (pid == 5001)
            return this.targetParticle();
        
        int index = this.getParticleIndex(pid, skip);
        if (index < 0 || index >= this.eventParticleList.count()) {
            return new Particle(pid, 0., 0., 0., 0., 0., 0.);
        }
        return eventParticleList.get(index);
    }
    /**
     * resizes the event array to given size. If event class is passed as an
     * argument to the method that fills it, this method will work faster, it minimizes
     * the object creation. This will substitute combination of event.clear() and event.addParticle().
     * @param size size of the new event list.
     */
    public void resize(int size){
        this.eventParticleList.resize(size);
    }
    
    public ParticleList getParticleList(){
        return this.eventParticleList;
    }
    
    public void writeToEvent(HipoEvent hipoEvent){
        SchemaFactory factory = hipoEvent.getSchemaFactory();
        if(factory.hasSchema("mc::event")==true){
            HipoGroup group = factory.getSchema("mc::event").createGroup(this.eventParticleList.count());
            for(int i = 0; i < this.eventParticleList.count();i++){
                Particle p = eventParticleList.get(i);
                group.getNode("pid").setShort(i, (short) p.pid());
                group.getNode("px").setFloat(i, (float) p.px());
                group.getNode("py").setFloat(i, (float) p.py());
                group.getNode("pz").setFloat(i, (float) p.pz());
                group.getNode("vx").setFloat(i, (float) p.vertex().x());
                group.getNode("vy").setFloat(i, (float) p.vertex().y());
                group.getNode("vz").setFloat(i, (float) p.vertex().z());
                group.getNode("parent").setByte(i, (byte) p.getParentParticle());
                group.getNode("status").setByte(i, (byte) p.getStatus());                
            }
            hipoEvent.addNodes(group.getNodes());
        }
    }
    
    public String toLundString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("%12d ",getParticleList().count()));
        
        /*%d %d %e %e %e %e %d %d %e\n", this.getParticleList().count(),
                1,1,0.0,0.0,getWeight(),this.beamParticle.e(),1,1,0.0));*/
        for(int i = 0; i < eventParameters.size(); i++){
            str.append(String.format(" %.5f ", eventParameters.get(i)));
        }
        str.append("\n");
        str.append(this.eventParticleList.toLundString());
        return str.toString();
    }
    
    
    public static PhysicsEvent filterEvent(PhysicsEvent event, int... status){
        PhysicsEvent filtered = new PhysicsEvent();
        filtered.beamParticle().copy(event.beamParticle());
        filtered.targetParticle.copy(event.targetParticle());
        for(int i = 0; i < event.getParticleList().count(); i++){
            int ps = event.getParticle(i).getStatus();
            boolean isAccepted = false;
            for(int s : status){
                if(ps==s) isAccepted = true;
            }
            if(isAccepted){
                Particle p = new Particle();
                p.copy(event.getParticle(i));
                filtered.addParticle(p);
            }
        }
        return filtered;
    }
    
    
    public void rotateZ(){
        double phi = Math.random()*2.0*Math.PI;
        rotateZ(phi);
    }
    
    public void rotateZ(double phi){        
        int ncount = this.eventParticleList.count();
        for(int i = 0; i < ncount; i++){
            this.eventParticleList.get(i).vector().rotateZ(phi);
        }      
        this.beamParticle.vector().rotateZ(phi);
        this.targetParticle.vector().rotateZ(phi);
    }
    
    public static void main(String[] args){
        LundReader  reader = new LundReader();
        reader.addFile("/Users/gavalian/Work/Software/project-1a.0.0/clasdispr.00.e11.000.emn0.75tmn.09.xs65.61nb.321.0013.dat");
        reader.open();
        Benchmark bench = new Benchmark();
        PhysicsEvent event = new PhysicsEvent();
        bench.addTimer("LUNDREADER");
        bench.resume("LUNDREADER");
        /*HipoWriter writer = new HipoWriter();
        writer.defineSchema("mc::event", 32111, "pid/S:px/F:py/F:pz/F:vx/F:vy/F:vz/F:parent/B:status/B");
        writer.open("test_lund.hipo");
        writer.setCompressionType(0);
        */
        EventWriter writer = new EventWriter("test_mc.hipo");
        //writer.getSchemaFactory().show();
        int icounter = 0;
        while(reader.nextEvent(event)==true){
            //while(reader.next()){
            //    event = reader.getEvent();
            //HipoEvent hipoEvent = writer.createEvent();
            //event.writeToEvent(hipoEvent);
            //System.out.println(event.toLundString());
            writer.writeEvent(event);
            icounter++;
        }
        bench.pause("LUNDREADER");
        writer.close();
        System.out.println(bench.toString());
        System.out.println("processed events = " + icounter);
    }
}
