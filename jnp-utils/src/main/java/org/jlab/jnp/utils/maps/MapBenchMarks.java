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
    
    public static void benchmarkJavaMapStatic(int count){
        Map<String,Integer> map = new HashMap<String,Integer>();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
        for(int m = 0; m < elements.size(); m++){
            //int random = (int) Math.random()*Integer.MAX_VALUE;                
            map.put(elements.get(m), 0);
        }
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;
                map.replace(elements.get(m), 5697);
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
          boolean status = false;
          for(int i = 0; i < count; i++){
              //map.clear();
              for(int m = 0; m < elements.size(); m++){
                    //int random = (int) Math.random()*Integer.MAX_VALUE;
                    //map.put(elements.get(m), 5697);
                    value = map.get(elements.get(m));
                    //status = map.containsKey(elements.get(m));
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
        boolean status = false;
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;                
                //map.add(elements.get(m), random);
                value = map.get(elements.get(m));
                //status = map.containsKey(elements.get(m));
            }
        }
    }
    
    public static void benchmarkCustomGetMap_2(int count){
        ObjectIntMap map = new ObjectIntMap(60,0.75F);
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
        for(int m = 0; m < elements.size(); m++){
            int random = (int) Math.random()*Integer.MAX_VALUE;                
            map.put(elements.get(m), random);
        }
        int[] value = new int[2];
        boolean status = false;
        for(int i = 0; i < count; i++){
            //map.clear();
            for(int m = 0; m < elements.size(); m++){
                //int random = (int) Math.random()*Integer.MAX_VALUE;                
                //map.add(elements.get(m), random);
                map.get(elements.get(m),value);
                status = map.contains(elements.get(m));
            }
        }
    }
    
    public static void benchmarkCustomGetMap_3(int count){
        IdentityIntMap map = new IdentityIntMap();
        List<String>   elements = Arrays.asList(new String[]{"a","b","c","d","e","f"});
        for(int m = 0; m < elements.size(); m++){
            int random = (int) Math.random()*Integer.MAX_VALUE;                
            map.put(elements.get(m), random);
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
    public static void printResults(Map<String,Long> results){
        for(Map.Entry<String,Long> entry : results.entrySet()){
            double time = ((double) entry.getValue() ) /1000.0;
            System.out.println(String.format("%25s : %9.2f sec",entry.getKey(),time));
        }
    }
    public static void benchmarkObjectMapGetInt(int factor){
        int iterations = 50000000;
        Map<String,Long> results = new HashMap<String,Long>();
        MapBenchMarks.benchmarkJavaMapGetInt(iterations);
        MapBenchMarks.benchmarkCustomMapGetInt(iterations);
        System.out.println(" warmup done - get Integer - from Int Int Map");
        long start_time = 0L;
        long end_time = 0L;
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkJavaMapGetInt(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("JAVA (Int) Get ", end_time-start_time);
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkCustomMapGetInt(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("Custom (Int) Get ", end_time-start_time);
        
        MapBenchMarks.printResults(results);
    }
    
    public static void benchmarkObjectMapPutStatic(int factor){
        int iterations = 50000000;
        Map<String,Long> results = new HashMap<String,Long>();
        MapBenchMarks.benchmarkJavaMapStatic(iterations);
        MapBenchMarks.benchmarkCustomMapStatic(iterations);
        System.out.println(" warmup done - Static String Integer map replace -");
        long start_time = 0L;
        long end_time = 0L;

        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkJavaMapStatic(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("JAVA (PUT) Static ", end_time-start_time);
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkCustomMapStatic(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("Custom (PUT) Static ", end_time-start_time);
        
        MapBenchMarks.printResults(results);
    }
    public static void benchmarkObjectMapGet(int factor){
        int iterations = 50000000;
        Map<String,Long> results = new HashMap<String,Long>();
        
        MapBenchMarks.benchmarkJavaGetMap(iterations);
        MapBenchMarks.benchmarkCustomGetMap(iterations);
        MapBenchMarks.benchmarkCustomGetMap_2(iterations);
        MapBenchMarks.benchmarkCustomGetMap_3(iterations);

        System.out.println(" warmup done - GET from String Integer Map - ");
        long start_time = 0L;
        long end_time = 0L;
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkJavaGetMap(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("JAVA ", end_time-start_time);
        
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkCustomGetMap(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("Custom 1 ", end_time-start_time);
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkCustomGetMap_2(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("Custom 2 ", end_time-start_time);
        
        start_time = System.currentTimeMillis();
        MapBenchMarks.benchmarkCustomGetMap_3(iterations*factor);
        end_time = System.currentTimeMillis();
        results.put("Custom 3 ", end_time-start_time);
        
        MapBenchMarks.printResults(results);
    }
    
    public static void main(String[] args){        
        
        int iterations = 50000000;
        long start_time = System.currentTimeMillis();
        //MapBenchMarks.benchmarkJavaMap(iterations);
        //MapBenchMarks.benchmarkCustomMapStatic(iterations);     
        
        //MapBenchMarks.benchmarkJavaMapPutInt(iterations);
        //MapBenchMarks.benchmarkCustomMap(50000000);
        
        //MapBenchMarks.benchmarkCustomMapPutInt(iterations);
        
        //MapBenchMarks.benchmarkJavaMapGetInt(iterations*2);
        //MapBenchMarks.benchmarkCustomMapGetInt(iterations*2);
        
        //MapBenchMarks.benchmarkCustomGetMap(iterations*3);
        //MapBenchMarks.benchmarkJavaGetMap(iterations*3);
        //MapBenchMarks.benchmarkCustomGetMap_2(iterations*3);
        //MapBenchMarks.benchmarkCustomGetMap_3(iterations*3);
        long end_time = System.currentTimeMillis();
        double time = (end_time-start_time)/1000.0;
        System.out.println(String.format("TIME = %.2f",time));
        int factor = 2;
        MapBenchMarks.benchmarkObjectMapGet(factor);
        MapBenchMarks.benchmarkObjectMapGetInt(factor);
        MapBenchMarks.benchmarkObjectMapPutStatic(factor);
    }
}
