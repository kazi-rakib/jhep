/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.cli.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class CliCommandDescriptor {
    
    String  system = "";
    String command = "";
    String description = "";
    String method = "";
    
    
    List<BasicInputConverter>  converters = new ArrayList<BasicInputConverter>();
    List<Class>                inputTypes = new ArrayList<Class>();
    
    
    public CliCommandDescriptor(String _system, String _command){
        system  = _system;
        command = _command;
    }
    
    public String getSystem(){ return system;}
    public String getCommand(){ return command;}
    public String getDescription(){ return description;}
    public String getMethod(){ return method;}
    
    public CliCommandDescriptor setSystem(String _system){ system = _system; return this;}
    public CliCommandDescriptor setCommand(String _command){ command = _command; return this;}
    public CliCommandDescriptor setDescription(String _desc){ description = _desc; return this;}
    public CliCommandDescriptor setMethod(String _method){ method = _method; return this;}
    
    public void addIntConverter(int _defaultValue){
        this.converters.add(new BasicInputConverter<Integer>(_defaultValue));
        this.inputTypes.add(Integer.TYPE);
    }
    
    public void addDoubleConverter(double _defaultValue){
        this.converters.add(new BasicInputConverter<Double>(_defaultValue));
        this.inputTypes.add(Double.TYPE);
    }
    
    public void addStringConverter(String _defaultValue){
        this.converters.add(new BasicInputConverter<String>(_defaultValue));
        this.inputTypes.add(String.class);
    }
    
    public void addConverter(BasicInputConverter converter){
        this.converters.add(converter);
    }
    
    public Class[]  getMethodInputs(){
        /*
        Class[] args = new Class[this.converters.size()];
        System.out.println(" class names = " + this.command + "  converters = " + this.converters.size());
        for(int i = 0; i < args.length; i++){
            String name = this.converters.get(i).getClass().getGenericSuperclass().getTypeName();
            System.out.println( i + " = " + name);
        }
        return args;*/
        Class[] inputTypesClass = new Class[this.inputTypes.size()];
        for(int i = 0; i < inputTypes.size();i++) inputTypesClass[i] = this.inputTypes.get(i);
        return  inputTypesClass;
    }
    
    public Object[] getInputs(String commandString){
        String[] tokens = commandString.split("\\s+");
        Object[] objArray = new Object[converters.size()];
        List<String> arguments = Arrays.asList(tokens);
        //arguments.remove(0);
        
        while(arguments.size()<this.converters.size()){
            arguments.add("!");
        }
        for(int i = 0; i < objArray.length;i++){
            objArray[i] = converters.get(i).valueOf(arguments.get(i));
        }
        return objArray;
    }
    
    public void execute(Object clazz, String line){
        
    }
    
    public String help(){
        StringBuilder  str = new StringBuilder();
        String cmd = String.format("%12s/%s", system,command);
        str.append(String.format(" %18s : %s", cmd,description));
        return str.toString();
    }
    
    public String usage(){
        StringBuilder  str = new StringBuilder();
        
        return str.toString();
    }
}
