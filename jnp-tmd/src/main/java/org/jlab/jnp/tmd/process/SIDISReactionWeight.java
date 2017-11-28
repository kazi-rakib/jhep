/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.Map;
import org.jlab.jnp.foam.IMCFunc;
import org.jlab.jnp.math.matrix.MatrixStore;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.reaction.PhaseSpace;
import org.jlab.jnp.physics.reaction.PhysicsReaction;
import org.jlab.jnp.physics.reaction.ReactionWeight;
import org.jlab.jnp.processes.KinematicsFactory;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.processes.SIDISEventGenerator;
import org.jlab.jnp.reader.EventWriter;
import org.jlab.jnp.reader.LundWriter;
import org.jlab.jnp.utils.data.ArrayUtils;
import org.jlab.jnp.utils.options.OptionParser;

/**
 *
 * @author gavalian
 */
public class SIDISReactionWeight implements ReactionWeight,IMCFunc {
    
    double alpha = 1.0/137.0;
    
    MatrixStore matrixStore = new MatrixStore();
    private PhaseSpace    phaseSpace = new PhaseSpace();
    private SIDISEventGenerator generator =  new SIDISEventGenerator();
    private SIDISPhysicsObservables physObservables = new SIDISPhysicsObservables();
    
    
    //SparseMatrix matrix = null;
    public SIDISReactionWeight(){

    }
    
    public SIDISPhysicsObservables getObservables(){
        return physObservables;
    }
    
    public final void loadResources(){
        matrixStore.readSparseMatrix(  "FUUT", "etc/data/FUUT_h.hipo");
        matrixStore.readSparseMatrix("FUUcos_bm", "etc/data/FUUcos_bm.hipo");
        matrixStore.readSparseMatrix("FUUcos_cahn", "etc/data/FUUcos_cahn.hipo");
    }
    
    public double kinematicFactor(double ebeam, double q2, double xb){
        double   y = KinematicsFactory.getNu(q2, xb)/ebeam;
        double eps = KinematicsFactory.getEpsilon(ebeam,q2, xb);
        double gamma = KinematicsFactory.getGamma(q2, xb);
        double  f1 =  alpha*alpha/(xb*y*q2);
        double  f2 = y*y/(2.0*(1.0-eps));
        double  f3 = (1.0 + gamma*gamma/(2.0*xb));
        return f1*f2*f3;
    }
    
    public void setPhaseSpace(PhaseSpace ps){
        phaseSpace = ps.copy();
    }
    
    public double getFuu(){
        return 1.0;
    }

    @Override
    public double getWeight(Map<String, Double> ps) {
        
        double ebeam = ps.get("E");
        double q2 = ps.get("q2");
        double xb = ps.get("xb");
        double phi = ps.get("phi");
        double pt  = ps.get("pt");
        
        double kin = kinematicFactor(ebeam,q2,xb);
        double eps = KinematicsFactory.getEpsilon(ebeam,q2, xb);
        double fuu_factor = Math.sqrt(2.0*eps*(1.0+eps));
        //double fuu = getFuu();
        
        double[] axis = new double[4];
        axis[0] = ps.get("xb");
        axis[1] = ps.get("q2");
        axis[2] = ps.get("z");
        axis[3] = ps.get("pt");
        
        double fuut = physObservables.getValue("FUUT", ps);
        
        //double fuut   = this.matrixStore.getMatrix("FUUT").evaluate(0, axis);
        //double fuucos_bm = this.matrixStore.getMatrix("FUUcos_bm").evaluate(0, axis);
        //double fuucos_cahn = this.matrixStore.getMatrix("FUUcos_cahn").evaluate(0, axis);
        //System.out.println(" factor = " + kin + "   = " + fuu_factor + "  FUUT = " + fuut + " FUU cos"
        // + fuucos);
        //double fuucos = fuucos_bm + fuucos_cahn;
        double cross = pt*kin*fuut;        
        //System.out.println(String.format(" KIN = %.8f FUUT = %.5f", cross,fuut));

        return cross;
    }
    
    public double getKinematicFactor(Map<String, Double> ps){
        return 1.0;
    }
    
    @Override
    public int getNDim() {
        return 6;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
       @Override
    public double getWeight(double[] par) {
        phaseSpace.setUnit(par);        
        double weight = this.getWeight(phaseSpace.getMap());
        PhysicsEvent event = generator.createEvent(phaseSpace.getMap());
        if(event.getParticleList().count()<2) {
            //System.out.println(" weight = zero");
            weight = 0.0;
        }
        //System.out.println(ArrayUtils.getString(par, "%.5f", " ") + " weight = " + weight);
        return weight;
    }
    

    public static void main(String[] args){
        
        OptionParser parser = new OptionParser("sidis-generator");
        
        parser.addOption("-n", "10000", "number of events to generate");
        parser.addOption("-max", "20000");
        parser.addRequired("-o", "output file name");
        parser.addOption("-e", "11.0", "beam energy");
        
        
        parser.parse(args);
        
        int     nEvents = parser.getOption("-n").intValue();
        int   maxEvents = parser.getOption("-max").intValue();
        String output = parser.getOption("-o").stringValue();
        
        
        //if(args.length>0){
        //    nEvents = Integer.parseInt(args[0]);
        //}
        
        
        SIDISEventGenerator generator = new SIDISEventGenerator();
        PhysicsReaction reaction = new PhysicsReaction();
        SIDISReactionWeight weight = new SIDISReactionWeight();
        
        
        weight.loadResources();
        
        reaction.setGenerator(generator);
        reaction.setReactionWeight(weight);
        
        PhaseSpace  phaseSpace = new PhaseSpace();
        
        double beam_energy = parser.getOption("-e").doubleValue();
        
        phaseSpace.add(   "E",  beam_energy,beam_energy);
        phaseSpace.add(  "q2",   1.0,  10.0);
        phaseSpace.add(  "xb", 0.025,  0.9995);
        phaseSpace.add(   "z", 0.025,  0.9995);
        phaseSpace.add(  "pt",   0.0,  1.0);
        phaseSpace.add( "phi", -Math.PI, Math.PI);
        
        reaction.setPhaseSpace(phaseSpace);
        SIDIS sidis = new SIDIS();
        //EventWriter writer = new EventWriter(output);
        //LundWriter writer = new LundWriter(output,maxEvents);
        
        EventWriter writer = new EventWriter(output);
        
        for(int i = 0; i < nEvents; i++){
            PhysicsEvent event = reaction.generate();
            //System.out.println(reaction.getPhaseSpace());
            
            sidis.processPhysicsEvent(event);
            
            event.setParameter(0, event.getWeight()*1000000);
            event.setParameter(1, 11.0);
            event.setParameter(2, sidis.getMap().get("q2"));
            event.setParameter(3, sidis.getMap().get("xb"));
            event.setParameter(4, sidis.getMap().get("z"));
            event.setParameter(5, sidis.getMap().get("pt"));
            event.setParameter(6, sidis.getMap().get("phi"));
            writer.appendMcEvent(event);
            writer.write();
            //writer.writeEvent(event.toLundString());
            //System.out.print(event.toLundString());
            //System.out.println(" weight = " + event.getWeight());
            //writer.reset();
            //writer.appendMcEvent(event);
            //writer.write();
            //System.out.println(sidis.toString());
        }
        writer.close();
    }

   

 
}
