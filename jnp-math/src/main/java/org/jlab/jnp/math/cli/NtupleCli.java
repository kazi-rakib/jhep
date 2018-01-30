/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.studio.DataStudio;
import org.jlab.groot.tree.TreeFile;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.cli.base.CliCommand;
import org.jlab.jnp.cli.base.CliSystem;
import org.jlab.jnp.readers.TextFileReader;

/**
 *
 * @author gavalian
 */
@CliSystem(system="ntuple",info="ntuple utilities for GROOT")
public class NtupleCli {
    
    //Map<Integer,IDataSet>  histogramStore = new HashMap<Integer,IDataSet>();
    Map<Integer,TreeFile>   ntupleStore = new HashMap<Integer,TreeFile>();
    Map<Integer,TCanvas>    canvasStore = new HashMap<Integer,TCanvas>();
    
    public void createHistogram(){
        
    }
    @CliCommand(command="open", info="open an ntuple file",
            defaults={"10","input.hipo"},
            descriptions={"ntuple ID","ntuple file name"})
    public void open(int id, String filename){
        TreeFile tree = new TreeFile(filename);
        System.out.println("  OPENED : " + filename);
        System.out.println(" ENTRIES : " + tree.getEntries());
        //ntupleStore.put(id, tree);
        DataStudio.getInstance().getNtupleStore().put(id, tree);
    }
    
    @CliCommand(command="scan", info="scan ntuple with given id",
            defaults={"10"},
            descriptions={"ntuple ID"})
    public void scan(int id){
        TreeFile tree = ntupleStore.get(id);
        System.out.println("SCAN COMPLETE");
    }
    
    @CliCommand(command="read", info="read text file into an ntuple",
            defaults={"!","!","!"},
            descriptions={"ntuple format (i.e x:y:z )","text file name", "output file name"})
    public void read(String format, String textFile, String outFile){
        TextFileReader reader = new TextFileReader();
        reader.open(textFile);
        TreeFile tree = new TreeFile(outFile, "T",format);
        int nbranches = tree.getListOfBranches().size();
        int nentries  = 0;
        int nlines    = 0;
        while(reader.readNext()==true){
            nlines++;
            int size = reader.entrySize();
            if(size==nbranches){
                float[] data = reader.getAsFloatArray(0, size);
                tree.addRow(data);
                nentries++;
            }
        }
        tree.close();
        System.out.println("NTUPLE : --> writing file " + outFile);
        System.out.println("NTUPLE : --> number of entries : " + nentries);
    }
    
    @CliCommand(command="plot", info="plot Variable from the ntuple",
            defaults={"10.x","!"},
            descriptions={"ntuple ID with the variable name","cut to be applied to the leafs"})
    public void plot(String id_withVariable, String treeCut){
        int index = id_withVariable.indexOf(".");
        String   idString = id_withVariable.substring(0, index);
        String expression = id_withVariable.substring(index+1, id_withVariable.length());
        System.out.println(" PLOTTING : " + idString + "  with " + expression);
        String cutString = treeCut;
        if(treeCut.compareTo("!")==0){
            cutString = "";
        }
        
        Integer ntID = Integer.parseInt(idString);
        if(expression.contains("%")==false){
           DataVector vec = DataStudio.getInstance().getNtupleStore().get(Integer.parseInt(idString)).getDataVector(expression, cutString);
           H1F h1 = H1F.create("H1", 100, vec);
           h1.setTitle(expression +" (" + cutString + ")");
           if(DataStudio.getInstance().getCanvasStore().isEmpty()==true){
               TCanvas canvas = new TCanvas("c1",500,500);
               DataStudio.getInstance().getCanvasStore().put("1", canvas);
           }
           DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(h1);
        } else {
            String data = expression.replace("%", ":");
            System.out.println(" PLOTTING DATA : " + data + " CUT : " + cutString);

            DataStudio.getInstance().getNtupleStore().get(Integer.parseInt(idString)).scanTree(data, cutString, -1, true);
            List<DataVector>  vectors = 
                    DataStudio.getInstance().getNtupleStore().get(Integer.parseInt(idString)).getScanResults();
            
            /*H2F h2 = new H2F("H2",
                    100,vectors.get(0).getMin(), vectors.get(0).getMax(),
                    100,vectors.get(1).getMin(), vectors.get(1).getMax()
            );
            */
            DataVector vecX = new DataVector();
            DataVector vecY = new DataVector();
            
            for(int i=0;i<vectors.get(0).size(); i++){
                if(vectors.get(2).getValue(i)>0.5){
                    vecX.add(vectors.get(1).getValue(i));
                    vecY.add(vectors.get(0).getValue(i));                    
                }
            }
            /*
            System.out.println(" SIZE = " + vectors.size());
            for(int i = 0; i < vectors.size(); i++){
                System.out.println("\t VECTOR " + i + "  size = " + vectors.get(i).size());
            }*/
            H2F h2 = H2F.create("H2", 100, 100, vecX, vecY);
            h2.setTitle(data + " (" + cutString + ")");
            if(DataStudio.getInstance().getCanvasStore().isEmpty()==true){
               TCanvas canvas = new TCanvas("c1",500,500);
               DataStudio.getInstance().getCanvasStore().put("1", canvas);
           }
           DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(h2);
        }
        
        
    }
    
}
