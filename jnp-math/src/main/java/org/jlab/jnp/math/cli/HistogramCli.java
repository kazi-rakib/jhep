/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.cli;

import java.util.Map;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.studio.DataStudio;
import org.jlab.groot.tree.TreeFile;
import org.jlab.jnp.cli.base.CliCommand;
import org.jlab.jnp.cli.base.CliSystem;

/**
 *
 * @author gavalian
 */
@CliSystem(system="histogram",info="histogram utilities for GROOT")
public class HistogramCli {
    
    @CliCommand(command="create", info="create a 1d histogram",
            defaults={"100","100","0.0","1.0"},
            descriptions={"histogram ID","number of bins","x axis minimum", "x axis maximum"})
    public void create(int id, int bins, double xmin, double xmax){
        H1F h1 = new H1F("H",bins,xmin,xmax);
        DataStudio.getInstance().getDataSetStore().put(id, h1);
    }
    @CliCommand(command="list", info="list histograms",
            defaults={},
            descriptions={})
    public void list(){
        Map<Integer,IDataSet> store = DataStudio.getInstance().getDataSetStore();
        for(Map.Entry<Integer,IDataSet> entry : store.entrySet()){
            System.out.println(String.format("%12s : SIZE = %8d", entry.getKey(),entry.getValue().getDataSize(0)));
        }
    }
    
    @CliCommand(command="plot", info="plot histograms",
            defaults={"10","!"},
            descriptions={"histogram id","plot options"})
    public void plot(int hid, String options){
        IDataSet ds = DataStudio.getInstance().getDataSetStore().get(hid);
        //DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(ds, options);
        DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(ds);
    }
    
    @CliCommand(command="divide", info="divide histograms",
            defaults={"10","11","12"},
            descriptions={"histogram id","plot options"})
    public void divide(int hid, int hnom, int hdenom){
        if(DataStudio.getInstance().getDataSetStore().containsKey(hid)==true){
            System.out.println("**** ERROR : histogram " + hid + " already exists");
            return;
        }
        if(DataStudio.getInstance().getDataSetStore().containsKey(hnom)==false||
                DataStudio.getInstance().getDataSetStore().containsKey(hdenom)==false){
            System.out.println("**** ERROR : histogram " + hdenom + " or " + hnom + " does not exist");
            return;
        }
        H1F h1 = (H1F) DataStudio.getInstance().getDataSetStore().get(hnom);
        H1F h2 = (H1F) DataStudio.getInstance().getDataSetStore().get(hdenom);
        //DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(ds, options);
        H1F hresult = new H1F("H1",h1.getXaxis().getNBins(),h1.getXaxis().min(), h1.getXaxis().max());
        hresult.add(h1);
        hresult.divide(h2);
        DataStudio.getInstance().getDataSetStore().put(hid, hresult);
    }
}
