/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.jnp.hipo.schema.Schema;
import org.jlab.jnp.hipo.schema.SchemaFactory;



/**
 *
 * @author gavalian
 */
public class HipoEventFilter {
    
    private final List<Integer> groupsExists = new ArrayList<Integer>();
    private final List<Integer> groupsOutput = new ArrayList<Integer>();

    public HipoEventFilter(){
        
    }
    
    public void addRequired(Integer group_id){
        groupsExists.add(group_id);
    }
    
    public void addRequired(List<Integer> groupList){
        groupsExists.addAll(groupList);
    }
    
    public void addOutput(Integer group_id){
        groupsOutput.add(group_id);
    }
    
    public void addOutput(List<Integer> groupList){
        groupsOutput.addAll(groupList);
    }
    
    public SchemaFactory getSchemaFactory(SchemaFactory factory){
        SchemaFactory filtered = new SchemaFactory();
        for(Schema schema : factory.getSchemaList()){
            Integer grp = schema.getGroup();
            if(this.groupsOutput.contains(grp)==true){
                filtered.addSchema(schema);
            }
        }
        return filtered;
    }
    
    public boolean isValid(HipoEvent event){
        for(Integer group : this.groupsExists){
            if(event.hasGroup(group)==false)
                return false;
        }
        return true;
    }
    
    public HipoEvent getEvent(HipoEvent event){
        HipoEvent filtered = new HipoEvent(event.getSchemaFactory());
        for(Integer group : this.groupsOutput){
            if(event.hasGroup(group)==true){
               Map<Integer,HipoNode> nodes = event.getGroup(group);
               List<HipoNode>     nodeList = new ArrayList<HipoNode>();
               for(Map.Entry<Integer,HipoNode> entry : nodes.entrySet()){
                   nodeList.add(entry.getValue());
               }
               filtered.addNodes(nodeList);
            }
        }
        return filtered;
    }
    
}
