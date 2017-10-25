/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.HashMap;
import java.util.Map;
import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.Vector3;
import org.jlab.jnp.processes.KinematicsFactory;
import org.jlab.jnp.processes.SIDIS;
import org.jlab.jnp.processes.SIDISEventGenerator;

/**
 *
 * @author gavalian
 */
public class SIDISEventGeneratorLOCAL {
    
    public PhysicsEvent createEvent(Map<String,Double> phaseSpace){
        
        PhysicsEvent event = new PhysicsEvent();
        Particle electron = KinematicsFactory.getElectron(phaseSpace.get("E"), phaseSpace.get("q2"), phaseSpace.get("xb"));
        event.setTargetParticle(new Particle(2212,0.0,0.0,0.0));
        event.setBeamParticle(new Particle(11,0.0,0.0,phaseSpace.get("E")));
        
        double nu = KinematicsFactory.getNu(phaseSpace.get("q2"), phaseSpace.get("xb"));
        double z  = phaseSpace.get("z");
        double pt = phaseSpace.get("pt");
        double phi = phaseSpace.get("phi");
        
        LorentzVector vcm = LorentzVector.from(event.beamParticle().vector());
        vcm.add(event.targetParticle().vector());
        
        Vector3 boost = vcm.boostVector();
        
        Vector3 cmBoost = Vector3.from(boost);
        
        cmBoost.negative();
        
        LorentzVector qstar = LorentzVector.from(event.beamParticle().vector());
        qstar.sub(electron.vector());
        qstar.boost(cmBoost);
        double MP = KinematicsFactory.MP;
        double MH = 0.139;
        
        double p = qstar.p();
        double ehadron = (p/MP)*z*nu*(Math.sqrt(1+MP*MP/(p*p))-
                   Math.sqrt(1.0-(pt*pt+MH*MH)/(z*z*nu*nu)));
        
        
        double nu2 = KinematicsFactory.getNu(event.beamParticle().vector(), electron.vector());
        double ehadron_lab = z*nu;
        if(ehadron_lab<MH){
            event.clear();
            return event;
        }
        
        double phadron_lab = Math.sqrt(ehadron_lab*ehadron_lab - MH*MH);
        if(phadron_lab < pt){
            event.clear();
            return event;
        }
        /*
        System.out.println("  Nu = " + nu + "  nu 2 = " + nu2 + "  z = " + (ehadron/nu2) );
        double phadron = Math.sqrt(ehadron*ehadron-MH*MH);
        if(phadron<pt){
            System.out.println(" pt to high " + phadron +  " pt = " + pt);
            event.clear();
            return event;
        }*/
        
        LorentzVector hlab = new LorentzVector();
        double theta   = Math.asin(pt/phadron_lab);
        
        hlab.setPxPyPzE(
                //pt*Math.cos(phi),
                //pt*Math.sin(phi),
                phadron_lab*Math.sin(theta)*Math.cos(phi), 
                phadron_lab*Math.sin(theta)*Math.sin(phi),
                phadron_lab*Math.cos(theta), ehadron_lab);
        
        /*System.out.println(" PT = " + hcm.vect().rho());
        System.out.println("HCM = " + theta + "  " + phadron + "  " + ehadron);
        hcm.print();
        hcm.boost(boost);
        System.out.println(" z = " + (hcm.e()/nu2) + "  e pi = " + hcm.e() + "  z nu = " + (z*nu));
        */
        LorentzVector w2 = LorentzVector.from(event.beamParticle().vector());
        w2.add(event.targetParticle().vector());
        w2.sub(electron.vector());
        LorentzVector mx2 = LorentzVector.from(w2);
        mx2.sub(hlab);
        
        if(w2.mass2()<=4.0||mx2.mass()<=1.4) {
            System.out.println(" no mx cut " + w2.mass2() + "  " + mx2.mass());
            event.clear();
            return event;
        }
        
        event.addParticle(electron);
        event.addParticle(new Particle(211,hlab.px(),hlab.py(),hlab.pz()));
        event.rotateZ();
        return event;
    }
    
    public static void main(String[] args){
        
        Map<String,Double> ps = new HashMap<String,Double>();
        //1.435793 0.089669 0.775709 0.226502 0.218733 2.395926
        if(args.length<7){
            System.out.println("usage : kinematics eb q2 xb phi z pt phih");
            System.out.println("\n");
            System.exit(0);
        }
        
        double phi = Double.parseDouble(args[3]);
        /*        ps.put("E", 11.0);
        ps.put("q2", 1.4357);
        ps.put("xb", 0.092);
        ps.put("z", 0.16);
        ps.put("pt", 0.218);
        ps.put("phi", -2.39);
        */    
        
        ps.put("E", Double.parseDouble(args[0]));
        ps.put("q2", Double.parseDouble(args[1]));
        ps.put("xb", Double.parseDouble(args[2]));
        ps.put("z", Double.parseDouble(args[4]));
        ps.put("pt", Double.parseDouble(args[5]));
        ps.put("phi", Double.parseDouble(args[6]));
        
        SIDISEventGenerator generator = new SIDISEventGenerator();
        PhysicsEvent event = generator.createEvent(ps);
        Particle  electron = event.getParticleByPid(11, 0);
        System.out.println("electron phi = " + electron.vector().phi());

        System.out.println(event.toLundString());
        event.rotateZ(-electron.vector().phi());
        System.out.println(event.toLundString());
        event.rotateZ(-phi);
        System.out.println(event.toLundString());
        
        for(Map.Entry<String,Double> entry : ps.entrySet()){
            System.out.println(entry.getKey() + "  " + entry.getValue());
        }
        //SIDIS sidis = new SIDIS();
        //sidis.processPhysicsEvent(event);        
        //System.out.println(sidis.toString());
        
        //LorentzVector vE = new LorentzVector();
        //vE.setPxPyPzM(0.0, 0.0, 11.0, 0.0005);
        //LorentzVector vEp = KinematicsFactory.getElectron(11.0, 1.43, 0.092).vector();
        
        //vE.sub(vEp);
        //System.out.println(" Q2 = " + vE.mass2());
    }
}
