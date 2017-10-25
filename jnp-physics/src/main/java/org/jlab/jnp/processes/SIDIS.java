/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.processes;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jlab.jnp.physics.EventFilter;
import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.Vector3;
import org.jlab.jnp.physics.map.MapProducer;

/**
 *
 * @author gavalian
 */
public class SIDIS implements MapProducer {
    
    Map<String,Double> processMap = new LinkedHashMap<String,Double>();
    EventFilter        filter     = new EventFilter("11:211:X+:X-:Xn");
    
    @Override
    public Map<String, Double> getMap() {
        return processMap;
    }

    @Override
    public boolean processPhysicsEvent(PhysicsEvent event) {
        
        if(filter.isValid(event)==false) return false;
        
        Particle vE  = event.beamParticle();
        Particle vT  = event.targetParticle();
        Particle vEp = event.getParticleByPid(11, 0);
        Particle vH  = event.getParticleByPid(211, 0);
        
        LorentzVector vecE  = LorentzVector.from(event.beamParticle().vector());
        LorentzVector vecT  = LorentzVector.from(event.targetParticle().vector());
        LorentzVector vecEp = LorentzVector.from(event.getParticleByPid(11, 0).vector());
        LorentzVector vecH  = LorentzVector.from(event.getParticleByPid(211, 0).vector());
        
        double rotation = -vecEp.phi();
        vecE.rotateZ(rotation);
        vecT.rotateZ(rotation);
        vecH.rotateZ(rotation);
        vecEp.rotateZ(rotation);
        
        double  Q2 = KinematicsFactory.getQ2(vE.vector(), vEp.vector());
        double  xb = KinematicsFactory.getXb(vE.vector(), vEp.vector());
        double  mu = KinematicsFactory.getNu(vE.vector(), vEp.vector());
        double   y = KinematicsFactory.getY(vE.vector(), vEp.vector());
        double   z = KinematicsFactory.getZ(vE.vector(), vEp.vector(),vH.vector());
        
        LorentzVector vcm = getCM(vecE,vecT,vecEp);
        
        Vector3 cmBoost = Vector3.from(vcm.boostVector());
        cmBoost.negative();
        
        LorentzVector   vecHcm = LorentzVector.from(vecH);
        LorentzVector vecQ2cm  = LorentzVector.from(vecE);
        
        vecQ2cm.sub(vecEp);
        
        //System.out.println(" VEC q2 cm = " + vecQ2cm.vect().toString());
        
        vecHcm.boost(cmBoost);
        vecQ2cm.boost(cmBoost);
        
        vecHcm.rotateY(vecQ2cm.theta());
        //System.out.println(" PT = " + vecHcm.vect().rho() + " phi = " + vecHcm.phi());
        
        //LorentzVector vhcm = hadronVectorCM(vE.vector(), vT.vector(), vH.vector());
       
        LorentzVector vhcm = LorentzVector.from(vecHcm);
        vhcm.rotateY(vecQ2cm.theta());
        /*
        double  pt = vhcm.vect().rho();
        double phi = vhcm.vect().phi() - vEp.vector().phi();
        */
        double  pt = vecHcm.vect().rho();
        double phi = vecHcm.phi();
        processMap.clear();
        processMap.put("q2", Q2);
        processMap.put("xb", xb);
        //processMap.put("y", y);
        processMap.put("z", z);
        processMap.put("pt", pt);
        processMap.put("phi", phi);
        return true;
    }
    
    public double getPhi(LorentzVector vE, LorentzVector vEp, LorentzVector vH){
        
        Vector3 vnorm = Vector3.from(vE.vect());
        vnorm.cross(vEp.vect());
        vnorm.unit();
        
        Vector3 vhadron = Vector3.from(vH.vect());
        
        double phi = 0.0;
        return phi;
    }
    
    public LorentzVector getCM(LorentzVector vE, LorentzVector vT, LorentzVector vEp){
        LorentzVector cm = LorentzVector.from(vE);
        cm.add(vT); cm.sub(vEp);
        return cm;
    }
    
    public LorentzVector hadronVectorCM(LorentzVector vE, LorentzVector vT, LorentzVector vH){
        LorentzVector vCM = LorentzVector.from(vE);
        vCM.add(vT);
        Vector3 boost = vCM.boostVector();
        boost.negative();
        LorentzVector vhCM = LorentzVector.from(vH);
        vhCM.boost(boost);
        return vhCM;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(Map.Entry<String,Double> entry : this.processMap.entrySet()){
            str.append(String.format("%f ", entry.getValue()));
        }
        //str.append("\n");
        return str.toString();
    }
}
