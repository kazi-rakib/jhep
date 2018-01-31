/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.studio.DataStudio;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.cli.base.CliCommand;
import org.jlab.jnp.cli.base.CliSystem;
import org.jlab.jnp.readers.TextFileReader;

/**
 *
 * @author gavalian
 */
@CliSystem(system="vector",info="vector utilities for GROOT")
public class VectorCli {
    @CliCommand(command="create", info="create a new vector",
            defaults={"a","0.0,1.0,2.0"},
            descriptions={"vector name","coma separated values for vector"})
    public void create(String name, String values){
        String[] tokens = values.split(",");
        DataVector vector = new DataVector();
        for(int i = 0; i < tokens.length; i++){
            try {
                double dv = Double.parseDouble(tokens[i].trim());
                vector.add(dv);
            } catch (Exception e) {
                System.out.println("** error ** format is wrong for entry [" + tokens[i] + "]");
            }
        }
        DataStudio.getInstance().getVectorStore().put(name, vector);
    }
    @CliCommand(command="plot", info="plot a vactor",
            defaults={"a","!"},
            descriptions={"vector name","cuts"})
    public void plot(String name, String cuts){
        
        if(DataStudio.getInstance().getCanvasStore().isEmpty()==true){
            TCanvas canvas = new TCanvas("c1",500,500);
            DataStudio.getInstance().getCanvasStore().put("1", canvas);
        }
        
        if(name.contains("%")==false){
            DataVector vec = DataStudio.getInstance().getVectorStore().get(name);
            GraphErrors graph = new GraphErrors();
            for(int i = 0; i < vec.getSize(); i++){
                graph.addPoint((double) (i+1), vec.getValue(i), 0.0, 0.0);
            }
            DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(graph);
        } else {
            String[] vectors = name.split("%");
            DataVector vecX = DataStudio.getInstance().getVectorStore().get(vectors[1]);
            DataVector vecY = DataStudio.getInstance().getVectorStore().get(vectors[0]);
            if(vecX.getSize()!=vecY.getSize()){
                System.out.println("*** error *** vector sizes are different for vectors " +
                        vectors[0] + " " + vectors[1]);
                return;
            }
            GraphErrors graph = new GraphErrors();
            for(int i =0; i < vecX.getSize(); i++){
                graph.addPoint(vecX.getValue(i), vecY.getValue(i), 0.0, 0.0);
            }
            DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(graph);
        }
    }
    
    @CliCommand(command="list", info="list all available vectors",
            defaults={},
            descriptions={})
    public void list(){
        Map<String,DataVector> store = DataStudio.getInstance().getVectorStore();
        for(Map.Entry<String,DataVector> entry : store.entrySet()){
            System.out.println(String.format("%12s : SIZE = %8d", entry.getKey(),entry.getValue().getSize()));
        }
    }
    
    @CliCommand(command="read", info="read vector from the file",
            defaults={"a","vector.txt","0"},
            descriptions={"format string","input file name","starting column in the file"})
    public void read(String format, String filename, int startPosition){
        TextFileReader reader = new TextFileReader();
        reader.open(filename);
        String[] tokens = format.split(":");
        List<DataVector> vectors = new ArrayList<DataVector>();
        
        for(int i = 0; i < tokens.length; i++){
            vectors.add(new DataVector());
        }
        
        while(reader.readNext()==true){
            double[] values = reader.getAsDoubleArray();
            if(values.length>=tokens.length+startPosition){
                for(int i = 0; i < tokens.length; i++){
                    vectors.get(i).add(values[i+startPosition]);
                }
            }
        }
        
        for(int i = 0; i < tokens.length; i++){
            System.out.println(" *** VECTOR/READ : adding a vector " + tokens[i] + " with size " 
                    + vectors.get(i).getSize());
            DataStudio.getInstance().getVectorStore().put(tokens[i], vectors.get(i));
        }
    }
}
