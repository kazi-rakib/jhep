/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.utils;

import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.hipo.io.HipoWriter;

/**
 *
 * @author gavalian
 */
public class HipoTests {
    
    public static float[] getFloatArray(){
        int size = (int) (Math.random()*300);
        size += 300;
        float[] array = new float[size];
        for(int i =0; i < array.length; i++){
            array[i] = (float) Math.random();
        }
        return array;
    }
    
    public static int[] getIntArray(float[] array){
        int[] buffer = new int[array.length];
        for(int i = 0; i < buffer.length; i++){
            buffer[i] = (int) (32000*array[i]);
        }
        return buffer;
    }
    public static short[] getShortArray(float[] array){
        short[] buffer = new short[array.length];
        for(int i = 0; i < buffer.length; i++){
            buffer[i] = (short) (32000*array[i]);
        }
        return buffer;
    }
    public static double[] getDoubleArray(float[] array){
        double[] buffer = new double[array.length];
        for(int i = 0; i < buffer.length; i++){
            buffer[i] = (double) (array[i]);
        }
        return buffer;
    }
    
    public static void compressionTests(){
        HipoWriter writerF = new HipoWriter();
        HipoWriter writerI = new HipoWriter();
        HipoWriter writerS = new HipoWriter();
        HipoWriter writerD = new HipoWriter();
        writerF.open("compression_test_F.hipo");
        writerI.open("compression_test_I.hipo");
        writerS.open("compression_test_S.hipo");
        writerD.open("compression_test_D.hipo");
        for(int i = 0; i < 25000; i++){
            float[] arrayF = HipoTests.getFloatArray();
            int[]   arrayI = HipoTests.getIntArray(arrayF);
            short[] arrayS = HipoTests.getShortArray(arrayF);
            double[] arrayD = HipoTests.getDoubleArray(arrayF);
            //System.out.println("size = " + arrayF.length + "  " + arrayI.length);
            HipoEvent eventF = new HipoEvent();
            HipoEvent eventI = new HipoEvent();
            HipoEvent eventS = new HipoEvent();
            HipoEvent eventD = new HipoEvent();
            HipoNode  nodeF  = new HipoNode(1200,1,arrayF);
            HipoNode  nodeI  = new HipoNode(1200,1,arrayI);
            HipoNode  nodeS  = new HipoNode(1200,1,arrayS);
            HipoNode  nodeD  = new HipoNode(1200,1,arrayD);
            eventF.addNode(nodeF);
            eventI.addNode(nodeI);
            eventS.addNode(nodeS);
            eventD.addNode(nodeD);
            writerF.writeEvent(eventF);
            writerI.writeEvent(eventI);
            writerS.writeEvent(eventS);
            writerD.writeEvent(eventD);
        }
        writerI.close();
        writerF.close();
        writerS.close();
        writerD.close();
    }
    public static void writerTest(){
        
    }
    public static void main(String[] args){
        HipoTests.compressionTests();
        /*
        HipoReader reader = new HipoReader();
        //reader.open("compression_test_S.hipo");
        reader.open("dictionary_test.hipo");
        int nevents = reader.getEventCount();
        for(int i = 0; i < 1; i++){
            HipoEvent event = reader.readEvent(i);
            event.showNodes();
        }*/
    }
}
