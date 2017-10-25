/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.processes;

import org.jlab.jnp.physics.LorentzVector;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.Vector3;



/**
 *
 * @author gavalian
 */
public class KinematicsFactory {
    
    public static double MP = 0.93827;
    /**
     * returns mu for given kinematics variables
     * @param q2 transferred momentum
     * @param xb Bjorken x
     * @return 
     */
    public static double getNu(double q2, double xb){
        //double pmass = PDGDatabase.getParticleMass(2212);
        return q2/(2.0*0.93827*xb);
    }
    /**
     * returns E' for given energy and kinematics variables.
     * @param E initial electron energy
     * @param q2 transferred momentum to proton
     * @param xb Bjorken x
     * @return 
     */
    public static double getEprime(double E, double q2, double xb){
        return E - KinematicsFactory.getNu(q2, xb);
    }
    /**
     * returns scattered electron angle for given kinematics
     * @param E initial electron energy
     * @param q2 transfered 4 momentum
     * @param xb Bjorken x
     * @return 
     */
    public static double getTheta(double E, double q2, double xb){
        double eprime = KinematicsFactory.getEprime(E, q2, xb);
        double left_side = q2/(4.0*E*eprime);
        double value = Math.sqrt(left_side);
        //System.out.println(" sinus = " + value + "  e-prime " + eprime );
        return 2.0*Math.asin(value);
    }
    
    public static boolean isValid(double E, double q2, double xb){
        double eprime = KinematicsFactory.getEprime(E, q2, xb);
        if(eprime<0) return false;
        double left_side = q2/(4.0*E*eprime);
        double     value = Math.sqrt(left_side);
        if(value>1.0) return false;
        return true;
    }
    
    public static double getEpsilon(double e, double q2, double xb){
        double nu = KinematicsFactory.getNu(q2, xb);
        double gamma = KinematicsFactory.getGamma(q2, xb);
        double y     = nu/e;
        double epsilon_1 = 1 - y - 0.25*y*y*gamma*gamma;
        double epsilon_2 = 1 - y + 0.5*y*y + 0.25*y*y*gamma*gamma;
        return epsilon_1/epsilon_2;
    }
    
    public static double getGamma(double q2, double xb){
        return 2.0*xb*0.93827/q2;
    }
    
    public static double getQ2(double E, double x, double y){
        return 2.0*MP*E*y*x;
    }
    
    /**
     * returns Q2 from vector of the beam and scattered electron
     * @param vE beam 4-vector 
     * @param vEp scattered electron 4-vector
     * @return Q^2
     */
    public static double getQ2(LorentzVector vE, LorentzVector vEp){
        LorentzVector vec = new LorentzVector(vE);
        vec.sub(vEp);
        double q2 = vec.mass2();
        return Math.abs(q2);
    }
    
    public static double getNu(LorentzVector vE, LorentzVector vEp){
        return (vE.e() - vEp.e());
    }
    
    public static double getXb(LorentzVector vE, LorentzVector vEp){
        double Q2 = KinematicsFactory.getQ2(vE, vEp);
        double mu = KinematicsFactory.getNu(vE, vEp);
        double xb = Q2/(2.0*KinematicsFactory.MP*mu);
        return xb;
    }
    
    public static double getY(LorentzVector vE, LorentzVector vEp){
        return KinematicsFactory.getNu(vE, vEp)/vE.e();
    }
    
    public static double getY(double e, double ep){
        return (e-ep)/e;
    }
    
    public static double getZ(LorentzVector vE, LorentzVector vEp, LorentzVector vH){
        return vH.e()/KinematicsFactory.getNu(vE, vEp);
    }
    /**
     * returns an electron for given beam energy, q2 and xb
     * @param E
     * @param q2
     * @param xb
     * @return 
     */
    public static Particle getElectron(double E, double q2, double xb){        
        double eprime = KinematicsFactory.getEprime( E, q2, xb);
        double theta  = KinematicsFactory.getTheta(  E, q2, xb);
        double pprime = Math.sqrt(eprime*eprime-0.0005*0.0005);
        //System.out.println("E-prime = " + eprime + "  theta = " + Math.toDegrees(theta));
        Vector3  vec = new Vector3();
        vec.setMagThetaPhi(pprime, theta, 0.0);
        return new Particle(11,vec.x(),vec.y(),vec.z(),0.0,0.0,0.0);
    }
    /**
     * returns magnetic moment of the proton
     * @param del2
     * @return 
     */
    public static double getGM_p(double del2){
        double denom = (1.0-del2/(0.84*0.84));
        double dipol = 1.0/(denom*denom);
        return (1.0+1.79)*dipol;
    }
    /**
     * returns magnetic moment for neutron
     * @param del2
     * @return 
     */
    public static double getGM_n(double del2){
        double denom = (1.0-del2/(0.84*0.84));
        double dipol = 1.0/(denom*denom);
        return -1.91*dipol;
    }
    /**
     * returns electric moment of neutron, it's
     * always 0, unless something has changed since
     * writing of this code.
     * @param del2
     * @return 
     */
    public static double getGE_n(double del2){
        return 0;
    }
    /**
     * returns electric moment of the proton
     * @param del2
     * @return 
     */
    public static double getGE_p(double del2){
        double denom = (1.0-del2/(0.84*0.84));
        double dipol = 1.0/(denom*denom);
        return dipol;
    }
    /**
     * Returns F1 function for U quark
     * @param del2
     * @return 
     */
    public static double getF1_u(double del2){
        double delim = del2/(4.0*MP*MP);
        double f1pn_1 = (KinematicsFactory.getGE_p(del2) - 
                delim*KinematicsFactory.getGM_p(del2))/(1.0-delim);
        double f1pn_2 = (KinematicsFactory.getGE_n(del2) - 
                delim*KinematicsFactory.getGM_n(del2))/(1.0-delim);
        return 2.0*f1pn_1 + f1pn_2; 
    }
    /**
     * returns F1 function for d quark
     * @param del2
     * @return 
     */
    public static double getF1_d(double del2){
        double delim = del2/(4.0*MP*MP);
        double f1pn_1 = (KinematicsFactory.getGE_p(del2) - 
                delim*KinematicsFactory.getGM_p(del2))/(1.0-delim);
        double f1pn_2 = (KinematicsFactory.getGE_n(del2) - 
                delim*KinematicsFactory.getGM_n(del2))/(1.0-delim);
        return 2.0*f1pn_2 + f1pn_1;
    }
}
