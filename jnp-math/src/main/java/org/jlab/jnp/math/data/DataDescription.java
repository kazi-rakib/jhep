/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class DataDescription {
    
    private Map<String,Integer>         descriptorMap = new LinkedHashMap<String,Integer>();
    private List<DataEntryDescription> descriptorList = new ArrayList<DataEntryDescription>();
    
    
    public DataDescription(){
        
    }
    
    public int getIndex(String name){
        if(this.descriptorMap.containsKey(name)==false) return -1;
        return this.descriptorMap.get(name);
    }
    
    
    public int getSize(){return this.descriptorList.size();}
    
    public DataEntryDescription getDescription(String name){
        if(this.descriptorMap.containsKey(name)==false) return null;
        return this.descriptorList.get(this.descriptorMap.get(name));
    }
    
    public DataEntryDescription getDescription(int index){
        return this.descriptorList.get(index);
    }
    
    public void addDescription(String name, String desc, String type){
        this.addDescription(new DataEntryDescription(name,desc,type));
    }
    
    public void addDescription(DataEntryDescription desc){
        this.descriptorList.add(desc);
        this.descriptorMap.put(desc.getName(), this.descriptorList.size()-1);
    }
    
    public static class DataEntryDescription {
        private String name = "";
        private String description = "";
        private String type = "int";
        
        public DataEntryDescription(){
            
        }
        
        public DataEntryDescription(String __name){
            name = __name;
        }
        
        public DataEntryDescription(String __name, String __desc){
            name = __name;
            description = __desc;
        }
        
        public DataEntryDescription(String __name, String __desc, String __type){
            name = __name;
            description = __desc;
            type = __type;
        }
        
        public String getName(){ return name;}
        public String getType() { return type;}
        public String getDescription() { return description;}
        
        public final DataEntryDescription setName(String __name) { name = __name; return this;}
        public final DataEntryDescription setType(String __type) { type = __type; return this;}
        public final DataEntryDescription setDescription(String __desc) { description = __desc; return this;}
    }
}
