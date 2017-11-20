/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class SIDISPhysicsObservables  {
    
    private Map<String,IPhysicsObservable> obsStore = new HashMap<String,IPhysicsObservable>();

    public SIDISPhysicsObservables(){
        addObservable(new PhysicsObservableFUUT());
    }

    public void addObservable(IPhysicsObservable obs){
        obsStore.put(obs.getName(), obs);
    }
    
    public double getValue(String observable, Map<String,Double> ps){
        double result = obsStore.get(observable).getValue(ps);
        return result;
    }
    
    public class PhysicsObservableFUUT implements IPhysicsObservable {
        private double[] parameters = new double[]{
            0.33, // 0 - average kt
            0.16, // 1 - average pt2
            3.00, // 2 - c_1mx_power
           -1.313, // 3 - c_x_power
            0.8, //   4 - c_z_const
            2.0, //   5 - 1mz_power
        }; // avkt
        
        @Override
        public String getName(){ return "FUUT";}
        
        @Override
        public double getValue(Map<String, Double> ps) {
            double x = ps.get("xb");
            double z = ps.get("z");
            double pt = ps.get("pt");
            
            double f1x = Math.pow((1.0-x), parameters[2])*Math.pow(x,parameters[3]);
            double d1z = parameters[4]*Math.pow(1-z, parameters[5]);
            double av_pth = z*z*parameters[0] + parameters[1];
            double dpt    = Math.exp(-pt*pt/av_pth)/av_pth/Math.PI;
            double fuut = x*f1x*d1z*dpt;
            return fuut;
        }
    }
    
}
