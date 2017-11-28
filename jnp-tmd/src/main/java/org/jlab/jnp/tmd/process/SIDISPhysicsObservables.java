/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.process;

import java.util.HashMap;
import java.util.Map;
import org.jlab.jnp.math.data.Parameters;

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
    
    public Parameters getParameters(){
        Parameters params = new Parameters();
        for(Map.Entry<String,IPhysicsObservable> entry : obsStore.entrySet()){
            params.addParametersAsGroup(entry.getValue().getParameters());
        }
        return params;
    }
    
    public void setParameters(Parameters params){
        System.out.println(" SETTING PARAMETERS ");
        System.out.println(params);
        for(Map.Entry<String,IPhysicsObservable> entry : obsStore.entrySet()){
            System.out.println(" FINDING GROUP " + entry.getValue().getName());
            
            Parameters obs_par = params.getParametersAsGroup(entry.getValue().getName());
            
            System.out.println(obs_par.toString());
            entry.getValue().getParameters().copyFrom(obs_par);
        }
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
        
        private final Parameters params = new Parameters("FUUT");
        
        public PhysicsObservableFUUT(){
            params.addParameter("av_kt", 0.33, 0.0, 1.0);
            params.addParameter("av_pt2", 0.16, 0.0, 1.0);
            params.addParameter("c_mx_power", 3.00, 0.0, 5.0);
            params.addParameter("c_x_power", -1.313, -5.0, 5.0);
            params.addParameter("c_z_const", 0.8, 0.0, 1.0);
            params.addParameter("mz_power", 2.0,0.0,6.0);
        }
        
        @Override
        public String getName(){ return "FUUT";}
        
        @Override
        public double getValue(Map<String, Double> ps) {
            double x = ps.get("xb");
            double z = ps.get("z");
            double pt = ps.get("pt");
            
            double f1x = Math.pow((1.0-x), params.getParameter("c_mx_power").getValue())*
                    Math.pow(x,params.getParameter("c_x_power").getValue());
            double d1z = params.getParameter("c_z_const").getValue()*
                    Math.pow(1-z, params.getParameter("mz_power").getValue());
            double av_pth = z*z*params.getParameter("av_kt").getValue() + 
                    params.getParameter("av_pt2").getValue();
            //double f1x = Math.pow((1.0-x), parameters[2])*Math.pow(x,parameters[3]);
            //double d1z = parameters[4]*Math.pow(1-z, parameters[5]);
            //double av_pth = z*z*parameters[0] + parameters[1];
            double dpt    = Math.exp(-pt*pt/av_pth)/av_pth/Math.PI;
            double fuut = x*f1x*d1z*dpt;
            return fuut;
        }

        @Override
        public Parameters getParameters() {
            return params;
        }
    }
    
}
