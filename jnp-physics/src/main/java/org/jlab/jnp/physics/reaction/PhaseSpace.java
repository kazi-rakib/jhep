/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.physics.reaction;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import org.jlab.jhep.utils.file.FileUtils;
import org.jlab.jhep.utils.json.Json;
import org.jlab.jhep.utils.json.JsonArray;
import org.jlab.jhep.utils.json.JsonObject;

/**
 *
 * @author gavalian
 */
public class PhaseSpace {
    
    Map<String,DimensionSpace>  params = new LinkedHashMap<String,DimensionSpace>();
    JPanel   spacePanel   = null;
    
    public PhaseSpace(){
        
    }
    
    public Set<String> getKeys(){
        return params.keySet();
    }
    
    public void add(DimensionSpace space){
        this.params.put(space.getName(), space);
    }
    
    public void add(String name, double min, double max){
        this.add(new DimensionSpace(name,min,max));
    }
    
    public void add(String name, double value, double min, double max){
        DimensionSpace dim = new DimensionSpace(name,min,max);
        dim.setValue(value);
        this.add(dim);
    }
    
    public DimensionSpace getDimension(String name){
        return this.params.get(name);
    }    
    
    public void initJsonFile(String filename){
        String jsonString = FileUtils.readFileAsString(filename);
        this.initJson(jsonString);
    }
    
    public void initJson(String jsonString){
        JsonObject  object = Json.parse(jsonString).asObject();
        
        if(object==null) {
            System.out.println("[Json::] error parsing json string.");
            return;
        }
        
        this.params.clear();
        
        JsonArray   phaseSpace = object.get("phasespace").asArray();
        
        if(phaseSpace==null) {
            System.out.println("[Json::] error: the object does not contain phase space");
            return;
        }
        
        int ndim = phaseSpace.size();
        for(int i = 0; i < ndim; i++){
            String name = phaseSpace.get(i).asObject().get("name").asString();
            //int    bins = phaseSpace.get(i).asObject().get("bins").asInt();
            double  min = phaseSpace.get(i).asObject().get("min").asDouble();
            double  max = phaseSpace.get(i).asObject().get("max").asDouble();
            DimensionSpace space = new DimensionSpace();
            space.set(name, min, max);
            this.params.put(name, space);
        }
    }
    
    public JPanel  createPanel(){
        this.spacePanel = new JPanel();
        this.spacePanel.setLayout(new FlowLayout());
        for(Map.Entry<String,DimensionSpace> item : this.params.entrySet()){
            item.getValue().createPanel();
            JPanel panel = item.getValue().getPanel();
            this.spacePanel.add(panel);
        }
        return this.spacePanel;
    }
    
    public Map<String,Double>  getMap(){
        Map<String,Double> map = new LinkedHashMap<String,Double>();
        for(Map.Entry<String,DimensionSpace> item : this.params.entrySet()){
            map.put(item.getKey(), item.getValue().getValue());
        }
        return map;
    }
    
    public static List<PhaseSpace> divide(String name, PhaseSpace ps){
        List<PhaseSpace> collection = new ArrayList<PhaseSpace>();
        int    nbins = ps.getDimension(name).getNBins();
        if(nbins==1) return collection;
        double   min = ps.getDimension(name).getMin();
        double   max = ps.getDimension(name).getMax();
        for(int i = 0; i < nbins; i++){
            double  low = min + i*(max-min)/nbins;
            double high = min + (i+1)*(max-min)/nbins;
            PhaseSpace space = new PhaseSpace();
            space.add(name, low, high);
            space.getDimension(name).setNBins(1);
            for(Map.Entry<String,DimensionSpace> entry : ps.params.entrySet()){
                if(entry.getKey().compareTo(name)!=0){
                    DimensionSpace dim = new DimensionSpace(
                            entry.getValue().getName(),entry.getValue().getMin(),
                            entry.getValue().getMax());
                    dim.setNBins(entry.getValue().getNBins());
                    space.add(dim);
                }
            }
            collection.add(space);
        }
        return collection;
    }
    
    public List<PhaseSpace> divide(){
        List<PhaseSpace> collection = new ArrayList<PhaseSpace>();
        Set<String> keys = this.params.keySet();
        /*for(String key : keys){
            List<PhaseSpace> list = divide(key);
            collection.addAll(list);
        }*/
        return collection;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(int c = 0; c < 88; c++) str.append("*");
        str.append("\n");
        str.append(String.format("* %-24s * %12s * %12s * %12s * %12s *",
                "Name", "Min","Max","Value", "Average"));
        str.append("\n");
        for(int c = 0; c < 88; c++) str.append("*");
        str.append("\n");
        for(Map.Entry<String,DimensionSpace> item : this.params.entrySet()){
            str.append(item.getValue().toString());
            str.append("\n");
        }
        for(int c = 0; c < 88; c++) str.append("*");
        str.append("\n");
        return str.toString();
    }
    
    public void show(){
        System.out.println(this.toString());
    }
    
    public void setRandom(){
        for(Map.Entry<String,DimensionSpace> entry : this.params.entrySet()){
            entry.getValue().setRandom();
        }        
    }
    
    public PhaseSpace  copy(){
        PhaseSpace space = new PhaseSpace();
        for(Map.Entry<String,DimensionSpace> entry : this.params.entrySet()){
            space.add(entry.getKey(), entry.getValue().getMin(), entry.getValue().getMax());
        }
        return space;
    }
    
    public boolean contains(Map<String,Double> values){
        for(Map.Entry<String,Double> entry : values.entrySet()){
            if(params.containsKey(entry.getKey())==false) return false;
            double v = entry.getValue();
            DimensionSpace ds = params.get(entry.getKey());
            if(v<ds.getMin()||v>ds.getMax()) return false;
        }
        return true;
    }
    
    public void addValues(Map<String,Double> values, double weight){
        for(Map.Entry<String,Double> entry : values.entrySet()){
            if(params.containsKey(entry.getKey())==true){
                this.params.get(entry.getKey()).addValue(entry.getValue(),weight);
            }
        }
    }
    
    public void resetCounter(){
        for(Map.Entry<String,DimensionSpace> entry : this.params.entrySet()){
            entry.getValue().resetCounter();
        }
    }
    
    public static void main(String[] args){
        PhaseSpace space = new PhaseSpace();
        space.add("x" , 0.0, 1.0);
        space.add("pt" , 0.5, 1.5);
        space.getDimension("x").setNBins(4);
        space.getDimension("pt").setNBins(4);
        
        List<PhaseSpace> psList = space.divide();
        System.out.println(" split SIZE = " + psList.size());
        for(PhaseSpace ps : psList){
            System.out.println(ps);
        }
        /*
        for(int i = 0; i < 300000; i++){
            space.setRandom();
            Map<String,Double> values = space.getMap();
            space.addValues(values,0.05);
            if(i%15000==0) 
                System.out.println(space);
        }
        space.resetCounter();
        System.out.println(space);*/
        /*
        JFrame frame = new JFrame();
        frame.add(space.createPanel());
        frame.pack();
        frame.setVisible(true);
        */
        /*
        PhaseSpace ps = new PhaseSpace();
        ps.initJson("{\"phasespace\":[{\"name\": \"q2\", \"min\": 1.2 , \"max\": 2.4},{\"name\": \"pt\", \"min\": 0.1 , \"max\": 2.0}]}");
        ps.show();*/
    }
}
