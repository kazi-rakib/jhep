/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.cli.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jhep.cli.base.CliClass;
import org.jlab.jhep.cli.base.CliCommand;
import org.jlab.jhep.cli.base.CliSystem;
import org.jlab.jhep.cli.base.CommandClassParser;

/**
 *
 * @author gavalian
 */
@CliSystem (system="sys", info="System commands")
public class SimpleCommands {
    
    public SimpleCommands(){
        
    }
    
    @CliCommand(
            command="ls", 
            info="provides list of the current directory",
            defaults={}, 
            descriptions={}
    )
    public void ls(){
        try {
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec("ls");
        } catch (IOException ex) {
            Logger.getLogger(SimpleCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @CliCommand(
            command="pwd", 
            info="show the current direcotry",
            defaults={},            
            descriptions={}
    )
    public void pwd(){
        try {
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec("pwd");
        } catch (IOException ex) {
            Logger.getLogger(SimpleCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @CliCommand(
            command="delay",
            info="displays a delay for given number of seconds",
            defaults={"5"},
            descriptions={"time interval to delay (in sec)"}
    )
    public void delay(int sec){
        System.out.println("deley for " + sec + " seconds");
        try {
            Thread.sleep(sec*1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("done...");
    }
    
    @CliCommand(
            command="toupper",
            info="converts string to upper case",
            defaults={"a"},
            descriptions={"string to convert to upper case"}
    )
    public void toupper(String input){
        String output = input.toUpperCase();
        System.out.println(output);
    }
    
    public static void main(String[] args){
        SimpleCommands command = new SimpleCommands();
        //CommandClassParser parser = new CommandClassParser();
        //parser.parse(command.getClass());        
        CliClass clazz = new CliClass();
        clazz.initWithClass(command.getClass());
        clazz.scanClass();
        clazz.help();
        //clazz.execute("delay", "!");
        clazz.execute("toupper", "troPicAl");
    }
}
