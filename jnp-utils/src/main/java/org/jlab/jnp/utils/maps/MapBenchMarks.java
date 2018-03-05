/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.utils.maps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class MapBenchMarks {
    
    public static void benchmarkJavaMap(int count){
        Map<String,Integer> map = new HashMap<String,Integer>();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
        
        for(int i = 0; i < count; i++){
            map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;
                map.put(elements.get(m), 5697);
            }
        }
        
    }
    
    public static void benchmarkJavaGetMap(int count){
        Map<String,Integer> map = new HashMap<String,Integer>();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
          for(int m = 0; m < elements.size(); m++){
                    //int random = (int) Math.random()*Integer.MAX_VALUE;
                    map.put(elements.get(m), 5697);
          }
          Integer value = 0;
          for(int i = 0; i < count; i++){
              //map.clear();
              for(int m = 0; m < elements.size(); m++){
                    //int random = (int) Math.random()*Integer.MAX_VALUE;
                    //map.put(elements.get(m), 5697);
                    value = map.get(elements.get(m));
                }
            }        
    }
    
    public static void benchmarkJavaMapPutInt(int count){
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        List<Integer>   elements = Arrays.asList(new Integer[]{1,3,6,7,18,25,32,43,67});        
        for(int i = 0; i < count; i++){
            map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;
                map.put(elements.get(m), 5678);
            }
        }
    }
    
    
    public static void benchmarkCustomMapPutInt(int count){
        //Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        IntIntMap map = new IntIntMap();
        List<Integer>   elements = Arrays.asList(new Integer[]{1,3,6,7,18,25,32,43,67});        
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;
                map.put(elements.get(m), 8672);
            }
        }
    }
    
    public static void benchmarkCustomGetMap(int count){
        StringIntHashMap map = new StringIntHashMap();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
        for(int m = 0; m < elements.size(); m++){
            int random = (int) Math.random()*Integer.MAX_VALUE;                
            map.add(elements.get(m), random);
        }
        int value = 0;
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;                
                //map.add(elements.get(m), random);
                value = map.get(elements.get(m));
            }
        }
    }
    public static void benchmarkCustomMap(int count){
        StringIntHashMap map = new StringIntHashMap();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
        
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                int random = (int) Math.random()*Integer.MAX_VALUE;                
                map.add(elements.get(m), random);
            }
        }
    }
    
    public static void benchmarkCustomMapGetInt(int count){
        //Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        IntIntMap map = new IntIntMap();
        List<Integer>   elements = Arrays.asList(new Integer[]{1,3,6,7,18,25,32,43,67});  
        for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;
                map.put(elements.get(m), 8672);
        }
        int[] value = new int[1];
        
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;
                map.get(elements.get(m), value);
            }
        }
    }
    public static void benchmarkJavaMapGetInt(int count){
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        List<Integer>   elements = Arrays.asList(new Integer[]{1,3,6,7,18,25,32,43,67});        
        
        for(int m = 0; m < elements.size(); m++){
            //int random = (int) Math.random()*Integer.MAX_VALUE;
            map.put(elements.get(m), 5678);
        }
        Integer value = 0;
        for(int i = 0; i < count; i++){
            for(int m = 0; m < elements.size(); m++){
            //int random = (int) Math.random()*Integer.MAX_VALUE;
            value = map.get(elements.get(m));
            }
        }
    }
    
    public static void benchmarkCustomMapStatic(int count){
        StringIntHashMap map = new StringIntHashMap();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});

        for(int m = 0; m < elements.size(); m++){
            //int random = (int) Math.random()*Integer.MAX_VALUE;                
            map.add(elements.get(m), 0);
        }

        
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                // int random = (int) Math.random()*Integer.MAX_VALUE;                
                map.replace(elements.get(m), 3689);
            }        
        }
    }
    
    public static void main(String[] args){
        
        
        int iterations = 50000000;
        long start_time = System.currentTimeMillis();
        //MapBenchMarks.benchmarkJavaMap(iterations);
        //MapBenchMarks.benchmarkCustomMapStatic(iterations);     
        
        //MapBenchMarks.benchmarkJavaMapPutInt(iterations);
        //MapBenchMarks.benchmarkCustomMap(50000000);
        
        //MapBenchMarks.benchmarkCustomMapPutInt(iterations);
        
        MapBenchMarks.benchmarkJavaMapGetInt(iterations*2);
        //MapBenchMarks.benchmarkCustomMapGetInt(iterations*2);
        
        //MapBenchMarks.benchmarkCustomGetMap(iterations);
        //MapBenchMarks.benchmarkJavaGetMap(iterations*3);
        long end_time = System.currentTimeMillis();
        double time = (end_time-start_time)/1000.0;
        System.out.println(String.format("TIME = %.2f",time));
        
        
    }
}
