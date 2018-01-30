/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.cli.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.cli.base.CliClass;
import org.jline.reader.impl.completer.StringsCompleter;

/**
 *
 * @author gavalian
 */
public class CliModuleManager {
    
    List<String> completerCommands = new ArrayList<String>();
    Map<String,CliClass>  commands = new LinkedHashMap<String,CliClass>();
    
    public CliModuleManager(){
        
    }
    
    /**
     * Initializes the module with given class name.
     * @param clazz_name 
     */
    public void initModule(String clazz_name){        
        try {
            Class module = Class.forName(clazz_name);
            CliClass clazz = new CliClass();
            clazz.initWithClass(module);
            clazz.scanClass();
            if(clazz.isInitialized()==true){
                System.out.println("[\033[32mINFO\033[0m] ***> init successfull for system : " + clazz.getSystemName());
                commands.put(clazz.getSystemName(),clazz);
                List<String> list = clazz.getCommandList();
                this.completerCommands.addAll(list);
                /*for(String item : list){
                    System.out.println(" ---> " + item);
                }*/
            } else {
                System.out.println("[\033[32mINFO\033[0m] ---> \033[31merror\033[0m initializing class : " + clazz_name);
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CliModuleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * String completer is used in JLine interface to tell terminal which
     * commands can be auto completed.
     * @return 
     */
    public StringsCompleter getCompleter(){
        String[] args = new String[this.completerCommands.size()];
        for(int i = 0; i < args.length; i++){
            args[i] = this.completerCommands.get(i);
        }
        return new StringsCompleter(args);
    }
    /**
     * returns a String containing only arguments from the command
     * @param commandLine
     * @return 
     */
    private String getArgumentList(String commandLine){
        String[] tokens = commandLine.trim().split("\\s+");
        if(tokens.length>1){
            StringBuilder str = new StringBuilder();
            for(int i = 1; i < tokens.length; i++){
                str.append(tokens[i]).append(" ");
            }
            return str.toString().trim();
        }
        return "";
    }
    /**
     * executes given command line. First separate the system and class
     * and command line.
     * @param commandLine
     * @return 
     */
    public boolean execute(String commandLine){
        String[] tokens = commandLine.trim().split("\\s+");
        if(tokens.length>0){
            if(tokens[0].contains("/")==true){
                String[] pair = tokens[0].split("/");
                if(this.commands.containsKey(pair[0])==true){
                    CliClass clazz = this.commands.get(pair[0]);
                    String    args = getArgumentList(commandLine);
                    clazz.execute(pair[1], args);
                }
            }
        }
        return true;
    }
    
    
    public void help(String system){
        if(this.commands.containsKey(system)==true){
            commands.get(system).help();
        }
    }
    /**
     * if command was not found in the dictionary, prints out the statement.
     * @param command 
     */
    public void printMessageUnrecognizedCommand(String command){
        System.out.println("\033[33m warning\033[0m: unrecognized command \"" + command + "\"");
    }
}

