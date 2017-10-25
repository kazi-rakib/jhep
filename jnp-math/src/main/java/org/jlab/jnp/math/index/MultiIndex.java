/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.index;

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
    public int offsetLength(){
        Integer size = 1;
        for(int i =0; i < indexList.size();i++) 
            size *= indexList.get(i);
        return size;
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
    
    public int getOffset(int... indices){
        int[] dimInfo = new int[rank+1];
        dimInfo[0] = rank;
        for(int i = 0; i < rank; i++) dimInfo[i+1] = indexList.get(i);
        int result = indices[0]*dimInfo[2] + indices[1];
        for (int i=2; i < dimInfo[0]; i++)
            result = result * dimInfo[i+1] + indices[i];
        return result;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("--> MULTIINDEX (%d) : ",offsetLength()));
        for(Integer nbins : this.indexList){
            str.append(String.format("%6d", nbins));
        }
        return str.toString();
    }
    
    public static void main(String[] args){
        MultiIndex index = new MultiIndex(5,3,4,14);
        System.out.println(index.toString());
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 3; j++){
                for(int k =0; k < 4; k++){
                    for(int c = 0; c < 14; c++)
                        System.out.println(i + " " +j+ " " + k + " " + c + "  " + index.getOffset(i,j,k,c));
                }
            }
        }
        
    }
}
