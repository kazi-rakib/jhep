/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.matrix;

/**
 *
 * @author gavalian
 */
public class CosineMatrix implements IMatrix {

    private int xBins = 50;
    private int yBins = 50;
    
    @Override
    public int getRank() {
        return 2;
    }

    @Override
    public int getBins(int dim) {
        int bins = 0;
        switch (dim){
            case 0 : bins = xBins; break;
            case 1 : bins = yBins; break;
            default: bins = 0;
        }
        return bins;
    }

    @Override
    public double getValue(int... bins) {
        if(bins.length!=2){
            System.out.println("[Matrix] ----> the rank is 2 and index length is " + bins.length);
            return 0.0;
        }
        double x = bins[0]*2.0*Math.PI/xBins;
        double y = bins[1]*2.0*Math.PI/yBins;
        double func = 4.0 + Math.cos(x) + Math.sin(y);
        return func;
    }
    
    
}
