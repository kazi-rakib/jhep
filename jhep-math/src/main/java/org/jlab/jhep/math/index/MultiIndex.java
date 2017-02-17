/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.math.index;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class MultiIndex {
    
    int rank = 1;
    
    List<Integer>  indexList = new ArrayList<Integer>();
    
    public MultiIndex(int _rank){
        this.rank = _rank;
    }
    
    public MultiIndex(int... dims){
        this.rank = dims.length;
        this.setDimensions(dims);
    }
    
    public final void setDimensions(int... dims){
        //->
        indexList.clear();
        for(int i = 0; i < dims.length; i++){
            indexList.add(dims[i]);
        }
    }
    
    private int to1Dfrom3D(int x, int y, int z){
        //--- text (z * xMax * yMax) + (y * xMax) + x;
        return 0;
    }
    
    private int[] to3Dfrom1D(int idx){
        /*final int z = idx / (xMax * yMax);
        idx -= (z * xMax * yMax);
        final int y = idx / xMax;
        final int x = idx % xMax;
        return new int[]{ x, y, z };
        */
        return new int[]{0,0,0};
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        return str.toString();
    }
}
