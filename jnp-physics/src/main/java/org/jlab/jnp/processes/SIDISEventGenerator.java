/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.processes;

import java.util.HashMap;
import java.util.Map;
import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.Vector3;
import org.jlab.jnp.physics.reaction.ReactionGenerator;

/**
 *
 * @author gavalian
 */
public class SIDISEventGenerator implements ReactionGenerator {
    
    @Override
    public PhysicsEvent createEvent(Map<String, Double> phaseSpace) {
        PhysicsEvent event = new PhysicsEvent();
        event.setTargetParticle(new Particle(2212,0.0,0.0,0.0));
        event.setBeamParticle(new Particle(11,0.0,0.0,phaseSpace.get("E")));
        
        double eprime = KinematicsFactory.getEprime(phaseSpace.get("E"), phaseSpace.get("q2"), phaseSpace.get("xb"));
        boolean valid = KinematicsFactory.isValid(phaseSpace.get("E"), phaseSpace.get("q2"), phaseSpace.get("xb"));
        if(valid==false) return event;
        /* Progress into calculations if eprime > 0*/
        
        Particle electron = KinematicsFactory.getElectron(phaseSpace.get("E"), phaseSpace.get("q2"), phaseSpace.get("xb"));
        LorentzVector w2 = LorentzVector.from(event.beamParticle().vector());
        w2.add(event.targetParticle().vector());
        w2.sub(electron.vector());
        
        if(w2.mass2()<4.0){
            return event;
        }
        
        //System.out.print(" electron = " ); electron.vector().print();
        
        double nu = KinematicsFactory.getNu(phaseSpace.get("q2"), phaseSpace.get("xb"));
        double z  = phaseSpace.get("z");
        double pt = phaseSpace.get("pt");
        double phi = phaseSpace.get("phi");
        
        LorentzVector vcm = LorentzVector.from(event.beamParticle().vector());
        vcm.add(event.targetParticle().vector());
        vcm.sub(electron.vector());
        
        Vector3 boost = vcm.boostVector();
        
        Vector3 cmBoost = Vector3.from(boost);
        
        cmBoost.negative();
        
        LorentzVector qstar = LorentzVector.from(event.beamParticle().vector());
        
        qstar.sub(electron.vector());
        //System.out.print( " phi = " + qstar.theta()*57.29 + " qstar = "); qstar.print();
        qstar.boost(cmBoost);
        
        double MP = KinematicsFactory.MP;
        double MH = 0.139;
        
        double p = qstar.p();
        double factor = 1.0-(pt*pt+MH*MH)/(z*z*nu*nu);
        
        if(factor<0) {
            event.clear();
            return event;
        }
        double ehadron = (p/MP)*z*nu*(Math.sqrt(1+MP*MP/(p*p))-
                   Math.sqrt(1.0-(pt*pt+MH*MH)/(z*z*nu*nu)));
        if(ehadron<MH){
            event.clear();
            return event;
        }
        double phadron = Math.sqrt(ehadron*ehadron-MH*MH);
        if(phadron<pt){
            event.clear();
            return event;
        }
        
        double theta_h = Math.asin(pt/phadron);
        Vector3 hvec = new Vector3();
        hvec.setMagThetaPhi(phadron, theta_h, phi);
        //System.out.println("\n\n****************************");
        //System.out.println(" HADRON VECTOR " +  hvec.toString());        
        hvec.rotateY(-qstar.theta());
        //System.out.println(" HADRON VECTOR ROTATED " +  hvec.toString());
        LorentzVector vHadron = new LorentzVector();
        vHadron.setPxPyPzM(hvec.x(), hvec.y(), hvec.z(), MH);
        vHadron.boost(boost);
        
        /*
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
        }*/
        /*
        System.out.println("  Nu = " + nu + "  nu 2 = " + nu2 + "  z = " + (ehadron/nu2) );
        double phadron = Math.sqrt(ehadron*ehadron-MH*MH);
        if(phadron<pt){
            System.out.println(" pt to high " + phadron +  " pt = " + pt);
            event.clear();
            return event;
        }*/
        /*
        LorentzVector hlab = new LorentzVector();
        double theta   = Math.asin(pt/phadron_lab);
        
        hlab.setPxPyPzE(
                //pt*Math.cos(phi),
                //pt*Math.sin(phi),
                phadron_lab*Math.sin(theta)*Math.cos(phi), 
                phadron_lab*Math.sin(theta)*Math.sin(phi),
                phadron_lab*Math.cos(theta), ehadron_lab);
        */
        /*System.out.println(" PT = " + hcm.vect().rho());
        System.out.println("HCM = " + theta + "  " + phadron + "  " + ehadron);
        hcm.print();
        hcm.boost(boost);
        System.out.println(" z = " + (hcm.e()/nu2) + "  e pi = " + hcm.e() + "  z nu = " + (z*nu));
        */
        LorentzVector w2L = LorentzVector.from(event.beamParticle().vector());
        w2L.add(event.targetParticle().vector());
        w2L.sub(electron.vector());
        
        
        
        LorentzVector mx2 = LorentzVector.from(w2L);
        mx2.sub(vHadron);
        
        if(mx2.mass()<=1.4) {
            //System.out.println(" no mx cut " + w2.mass2() + "  " + mx2.mass());
           event.clear();
            return event;
        }
        
        event.addParticle(electron);
        event.addParticle(new Particle(211,vHadron.px(),vHadron.py(),vHadron.pz()));
        event.rotateZ();
        return event;
    }
    
    public static void main(String[] args){
        
  
        /*
        Map<String,Double> ps = new HashMap<String,Double>();
        ps.put("E", 10.6);
        ps.put("q2", 2.0);
        ps.put("xb", 0.2);
        ps.put("z", 0.5);
        ps.put("pt", 0.4);
        ps.put("phi", 1.5708);
        //0.2 2.0 0.0 0.5 0.4 3.142
        SIDISEventGenerator eg = new SIDISEventGenerator();
        
        PhysicsEvent event = eg.createEvent(ps);
        System.out.println(event.toLundString());
        
        LorentzVector e  = LorentzVector.from(event.getParticleByPid(11, 0).vector());
        LorentzVector pi = LorentzVector.from(event.getParticleByPid(211, 0).vector());
        LorentzVector q  = new LorentzVector(0.0,0.0,10.6,10.6);
        LorentzVector t  = new LorentzVector(0.0,0.0,0.0,0.938);
        q.add(t);
        q.sub(e);
        
        
        LorentzVector piLab = LorentzVector.from(pi);
        
        Vector3 unboost = q.boostVector();
        unboost.negative();
        
        Vector3 z = Vector3.from(q.vect());
        z.unit();
        Vector3 y = new Vector3(0.0,1.0,0.0);
        Vector3 x = y.cross(z);
        System.out.println("x - prime = " + x);
        System.out.println("y - prime = " + y);
        System.out.println("z - prime = " + z);       
                
        //pi.boost(unboost);
        
        System.out.println("pt = " + pi.vect());
        System.out.println("e  = " + e.vect());
        System.out.println("q  = " + q.vect());
        System.out.println("b  = " + unboost);
        
        pi.boost(unboost);
        
        System.out.println("pi-cm = " + pi.vect());
        
        double pi_x = pi.vect().dot(x);
        double pi_y = pi.vect().dot(y);
        double pi_z = pi.vect().dot(z);
        
        double pt = Math.sqrt(pi_x*pi_x + pi_y*pi_y);
        double phi = Math.atan2(pi_y, pi_x);
        System.out.println(" USING x-y-z pt = " + pt + "  phi* = " + phi);
        
        Vector3 pi_d = q.vect().cross(piLab.vect());
        double dot = pi_d.dot(y)/pi_d.mag();
        System.out.println("USIN cross theta angle = " + Math.acos(dot));*/
    }
}
