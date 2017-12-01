/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author gavalian
 */
public class Parameters {
    
    private String name = "unknown";
    private final Map<String,Parameter> parameters = new LinkedHashMap<String,Parameter>();
    
    public Parameters(){
        
    }
    
    public Parameters(String __name){
        name = __name;
    }
    
    
    public String getName(){ return name;}
    /**
     * copies content of the parameters passed as argument to the current parameters
     * the copies of each parameter is created
     * @param pars reference parameters
     */
    public void copyFrom(Parameters pars){
        this.parameters.clear();
        for(Map.Entry<String,Parameter> entry : pars.getParameters().entrySet()){
            this.addParameter(entry.getValue(), true);
        }
    }
    
    public void addParameter(Parameter par, boolean isCopy){
        if(isCopy==true){
            Parameter param = new Parameter();
            param.copyFrom(par);
            this.parameters.put(param.getName(), param);
        } else {
            this.parameters.put(par.getName(), par);
        }
    }
    
    public void addParameter(String __pname, double __min, double __max){ 
        parameters.put(__pname, new Parameter(__pname,__min,__max));
    }
    
    public void addParameter(String __pname,double __value,  double __min, double __max){ 
        parameters.put(__pname, new Parameter(__pname,__value,__min,__max));
    }
    
    public Parameter getParameter(String name){ return parameters.get(name);}
    
    public Map<String,Parameter> getParameters(){ return parameters;}
    
    public double[] getAsArray(){
        double[] result = new double[parameters.size()];
        int counter = 0;
        for(Map.Entry<String,Parameter> entry : parameters.entrySet()){
            result[counter] = entry.getValue().getValue();
            counter++;
        }
        return result;
    }
    /**
     * adds all parameters in the passes array to the current array with 
     * names changes as parameters(name):parameter(name)
     * @param pars parameters to add as a group
     */
    public void addParametersAsGroup(Parameters pars){
        for(Map.Entry<String,Parameter> entry : pars.getParameters().entrySet()){
            String parname = String.format("%s:%s", pars.getName(), entry.getKey());
            Parameter par = new Parameter();
            par.copyFrom(entry.getValue());
            par.setName(parname);
            this.addParameter(par, false);
        }
    }
    /**
     * returns a subset of parameters that names start with group_name
     * the format is group_name:parameter_name, the group name is removed
     * from the parameter name and set as the name of parameters class
     * @param group_name group name for the subset
     * @return 
     */
    public Parameters getParametersAsGroup(String group_name){
        String group_start = group_name + ":";
        Parameters  result = new Parameters(group_name);
        for(Map.Entry<String,Parameter> entry : parameters.entrySet()){
            System.out.println("Parameter name = " + entry.getKey() + " starts with = " + entry.getKey().startsWith(group_name));
            if(entry.getKey().startsWith(group_name)==true){
                String parname = entry.getKey().substring(group_start.length(), entry.getKey().length());
                Parameter par = new Parameter();
                par.copyFrom(entry.getValue());
                par.setName(parname);
                result.addParameter(par, false);
            }
        }
        return result;
    }
    
    public void setRandom(){
        for(Map.Entry<String,Parameter> entry : parameters.entrySet()){
            entry.getValue().setRandom();
        }
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        System.out.println(String.format("******* PARAMETER SET (%s) ******* SIZE = %d",
                getName(),this.parameters.size()));
        for(Map.Entry<String,Parameter> entry : parameters.entrySet()){
            str.append(entry.getValue().toString()).append("\n");
        }
        return str.toString();
    }
}
