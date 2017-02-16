/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.cli.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class CliClass {
    
    private Object cliClazz = null;    
    private Map<String,CliCommandDescriptor> descriptors = new LinkedHashMap<String,CliCommandDescriptor>();
    private String systemName = "";
    private String systemInfo = "";
    private Boolean initialized = false;
    
    public CliClass(){
        
    }
    
    public void initWithClass(String className){
        
    }
    
    public void initWithClass(Class clazz){        
        try {
            cliClazz = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CliClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String getSystemName(){return systemName;}
    public String getSystemInfo(){return systemInfo;}
    public Boolean isInitialized(){return this.initialized;}
    
    public List<String> getCommandList(){
        List<String> commandList = new ArrayList<String>();
        for(Map.Entry<String,CliCommandDescriptor> entry : this.descriptors.entrySet()){
            commandList.add(this.getSystemName() + "/" + entry.getValue().getCommand());
        }
        return commandList;
    }
    public void scanClass(){
        
        this.initialized = false;
        CliSystem clazzInfo = (CliSystem) cliClazz.getClass().getAnnotation(CliSystem.class);
        if(clazzInfo==null){
            System.out.println(">>>> invalid class : " + cliClazz.getClass().getName());
            return;
        }
        
        systemName = clazzInfo.system();
        systemInfo = clazzInfo.info();
        this.descriptors.clear();
        
        Method[] methods = cliClazz.getClass().getMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(CliCommand.class)==true){
                Type[] types = method.getParameterTypes();
                Annotation[] annotations = method.getDeclaredAnnotations();
                CliCommand ann = (CliCommand) annotations[0];
                String system = systemName;
                String command    = ann.command();
                String info    = ann.info();
                String[] args     = ann.descriptions();
                String[] defaults = ann.defaults();
                CliCommandDescriptor desc = new CliCommandDescriptor(systemName,command);
                desc.setMethod(method.getName()).setDescription(info);
                for(int k = 0; k < types.length; k++){
                    //System.out.println(">>> type name = [" + types[k].getTypeName() + "]");
                    if(types[k].getTypeName().compareToIgnoreCase("int")==0){
                        //desc.addConverter(new BasicInputConverter<Integer>("a",args[k],Integer.parseInt(defaults[k])));
                        desc.addIntConverter(Integer.parseInt(defaults[k]));
                    }
                    if(types[k].getTypeName().compareToIgnoreCase("double")==0){
                        //desc.addConverter(new BasicInputConverter<Integer>("a",args[k],Integer.parseInt(defaults[k])));
                        desc.addDoubleConverter(Double.parseDouble(defaults[k]));
                    }
                    if(types[k].getTypeName().compareToIgnoreCase("java.lang.String")==0){
                        //desc.addConverter(new BasicInputConverter<Integer>("a",args[k],Integer.parseInt(defaults[k])));
                        desc.addStringConverter(defaults[k]);
                    }
                }
                this.descriptors.put(desc.getCommand(),desc);
                //System.out.println(" SIZE TYPES = " + types.length + " " + args.length + " " + defaults.length);
                
            }
        }
        if(this.descriptors.size()>0) {
            initialized = true;
        } else {
            System.out.println(" no decalred methods");
        }
    }
    
    
    public void help(){
        for(Map.Entry<String,CliCommandDescriptor> desc : this.descriptors.entrySet()){
            System.out.println(desc.getValue().help());
        }
    }
    
    public void execute(String command, String arguments){
        
        CliCommandDescriptor desc = this.descriptors.get(command);
        
        Object[] array   = desc.getInputs(arguments);
        Class[]  args    = desc.getMethodInputs();
        System.out.println(" LENGTH = " + args.length);
        try {
            Method method = this.cliClazz.getClass().getDeclaredMethod(command, args);
            method.invoke(cliClazz, array);
        } catch (NoSuchMethodException | SecurityException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(CliClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
