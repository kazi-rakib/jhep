/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.utils;

import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.DataBankHipo;
import org.jlab.jnp.hipo.io.DataEventHipo;
import org.jlab.jnp.hipo.io.HipoReader;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.jnp.utils.benchmark.Benchmark;

/**
 *
 * @author gavalian
 */
public class HipoTests {
    
    public static void readerTest(String filename){
         HipoReader reader = new HipoReader();
        reader.open(filename);
        int nrecords = reader.getRecordCount();
        DataEventHipo event = new DataEventHipo();
        DataBankHipo  bank  = new DataBankHipo();
        
        Benchmark  bench = new Benchmark();
        bench.addTimer("HIPO-READER");

        for(int r = 0; r < 1; r++){
            bench.resume("HIPO-READER");
            reader.readRecord(r+1);
            int nevents = reader.getRecordEventCount();
            //for(int ev = 0; ev < nevents-1; ev++){
            for(int ev = 0; ev < 20; ev++){
                //System.out.println("--- event " + ev);
                reader.readRecordEvent(event, ev+1);
                event.getDataBank(bank, "mc::event");
                bank.show();
                //event.showByteBuffer();
            }
            bench.pause("HIPO-READER");
        }
        System.out.println(bench.toString());
    }
    public static void writerTest(){
        
    }
    public static void main(String[] args){
        HipoTests.readerTest("/Users/gavalian/Work/DataSpace/clas12/mc/clas_dis_mcdata.hipo");
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
