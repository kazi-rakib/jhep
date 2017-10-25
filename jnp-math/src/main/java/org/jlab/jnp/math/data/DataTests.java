/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class DataTests {
    public static void main(String[] args){
        Map<Long,Double> map = new LinkedHashMap<Long,Double>();
        List<Long> list = new ArrayList<Long>();
        List<Float> listF = new ArrayList<Float>();
        for(int i = 0; i < 40*40*40*40; i++){
            Long key = (long) i+2;
            Float random = (float) Math.random();
            //map.put(key, random);
            list.add(key);
            listF.add(random);
        }
        Long size = (long) 40*40*40*40;
        Long size_MB = size/1024/1024;
        System.out.println(" LONG SIZE = " + size_MB + " mb");
        int counter = 0;
        while(true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataTests.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("iteration " + counter + "  TOTAL ELEMENTS = " + size);
            counter++;
        }
    }
}
