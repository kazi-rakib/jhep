/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.studio.DataStudio;
import org.jlab.groot.ui.TCanvas;
import org.jlab.jnp.cli.base.CliCommand;
import org.jlab.jnp.cli.base.CliSystem;
import org.jlab.jnp.detector.EventSelectors;

/**
 *
 * @author gavalian
 */
@CliSystem(system="reaction",info="Physics Reaction Utilities")
public class ReactionCli {
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream printStream = null;// new PrintStream(baos);
    PrintStream printStreamOut = null;
    
    private Map<Integer,EventSelectors> reactionSelectors = new HashMap<Integer,EventSelectors>();
    
    public ReactionCli(){
        printStream = new PrintStream(baos);
        printStreamOut = System.out;
    }
    
    private void redirectOutput(){
        System.setOut(this.printStream);
    }
    
    private void restoreOutput(){
        System.setOut(this.printStreamOut);
    }
    @CliCommand(command="create", info="create a reaction",
            defaults={"10","11:X+:X-:Xn","6.4"},
            descriptions={"reaction ID","event filter","beam energy"})
    public void create(int id, String filter, double energy){
        EventSelectors selector = new EventSelectors();
        selector.setFilter(filter);
        selector.setEnergy(energy);
        this.reactionSelectors.put(id, selector);
    }
    
    @CliCommand(command="file", info="set file for the reaction",
            defaults={"10","input.hipo"},
            descriptions={"reaction ID","file name"})
    public void file(int id, String filename){
        this.redirectOutput();
        this.reactionSelectors.get(id).setFile(filename);
        this.restoreOutput();
        System.out.println(" ***** REACTION/FILE opened file : " + filename);
    }
    
    @CliCommand(command="particle", info="set file for the reaction",
            defaults={"10","ep", "[11]","p"},
            descriptions={"reaction ID", "variable name" ,"particle selection string","property"})
    public void particle(int id, String name, String particle, String property){
        reactionSelectors.get(id).addSelector(name, particle, property);
    }
    
    private List<String> decouple(String argument){
        int index = argument.indexOf(".");
        String   idstr = argument.substring(0, index);
        String expression = argument.substring(index+1, argument.length());
        
        if(expression.contains("%")==false){
            return Arrays.asList(idstr,expression);
        } else {
            String expression2D = expression.replace("%", ":");
            return Arrays.asList(idstr,expression2D);
        }
        
        //return new ArrayList<String>();
    }
    
    @CliCommand(command="plot", info="set file for the reaction",
            defaults={"10.ep", "!","-1","100"},
            descriptions={"reaction id with variable name", "selection cut",
                "limit on number of events","number of bins"}
    )    
    public void plot(String idString, String cut, int limit, int nbins){
        
        
        this.redirectOutput();
        if(DataStudio.getInstance().getCanvasStore().containsKey("1")==false){
            TCanvas canvas = new TCanvas("c1",500,500);
               DataStudio.getInstance().getCanvasStore().put("1", canvas);
        }
        
        List<String> dataArgs = this.decouple(idString);
        
        String cutString = "";
        if(cut.compareTo("!")!=0) cutString = cut;
        
        /*int index = idString.indexOf(".");
        String   idstr = idString.substring(0, index);
        String expression = idString.substring(index+1, idString.length());
        */
        //String[] tokens = idString.split(".");
        //System.out.println(" PLOTTING [" + tokens[0] + "] [" + tokens[1] + "] CUT STRING = " + cutString );        
        if(dataArgs.get(1).contains(":")==false){
            int rid = Integer.parseInt(dataArgs.get(0));
            DataVector vec = reactionSelectors.get(rid).getDataVector(dataArgs.get(1),cutString, limit);
            System.out.println(" QUERY : Entries = " + vec.getSize() + " , Min = " + vec.getMin()
                    + " , Max = " + vec.getMax());
            H1F h = H1F.create("H1", nbins, vec);
            DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(h);
        } else {
            System.out.println(" PLOTTING 2D REACIONT VARIABLES " + dataArgs.get(1));
            int rid = Integer.parseInt(dataArgs.get(0));
            reactionSelectors.get(rid).scanTree(dataArgs.get(1), cutString, limit, true);
            List<DataVector>  vectors = 
                    reactionSelectors.get(rid).getScanResults();
            DataVector vecX = new DataVector();
            DataVector vecY = new DataVector();
            System.out.println("VECTORS SIZE = " + vectors.size() + "  LENGTH = " + vectors.get(1).getSize());
            for(int i=0;i<vectors.get(0).size(); i++){
                if(vectors.get(2).getValue(i)>0.5){
                    vecX.add(vectors.get(1).getValue(i));
                    vecY.add(vectors.get(0).getValue(i));                    
                }
            }
            H2F h2 = H2F.create("H2", nbins, nbins, vecX, vecY);
            h2.setTitle(dataArgs.get(1) + " (" + cutString + ")");
            if(DataStudio.getInstance().getCanvasStore().isEmpty()==true){
               TCanvas canvas = new TCanvas("c1",500,500);
               DataStudio.getInstance().getCanvasStore().put("1", canvas);
           }
           DataStudio.getInstance().getCanvasStore().get("1").getCanvas().drawNext(h2);
           this.restoreOutput();
        }
        
    }
}
