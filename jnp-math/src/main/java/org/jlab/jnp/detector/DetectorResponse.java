/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.detector;

/**
 *
 * @author gavalian
 */
public class DetectorResponse {
    
    private DetectorDescriptor desc = new DetectorDescriptor();
    private double     responseTime = 0.0;
    private double     responseEnergy = 0.0;
    private double       responsePath = 0.0;
    
    public DetectorResponse(){
        
    }
    
    public DetectorResponse(DetectorType type, int sector, int layer, int component){
        desc.setType(type);
        desc.setSectorLayerComponent(sector, layer, component);
    }
    
    public DetectorDescriptor getDescriptor(){ return desc;}
        
    public DetectorResponse setTime(double time){ this.responseTime = time; return this;}
    public DetectorResponse setPath(double path){ this.responseTime = path; return this;}
    public DetectorResponse setEnergy(double energy){ this.responseTime = energy; return this;}
        
    public double getTime() { return this.responseTime;}
    public double getPath() { return this.responsePath;}
    public double getEnergy() { return this.responseEnergy;}
}
