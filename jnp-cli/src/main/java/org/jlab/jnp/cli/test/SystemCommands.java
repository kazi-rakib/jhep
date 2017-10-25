/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.cli.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.cli.base.CliClass;
import org.jlab.jnp.cli.base.CliCommand;
import org.jlab.jnp.cli.base.CliSystem;
import org.jlab.jnp.cli.base.CommandClassParser;

/**
 *
 * @author gavalian
 */
@CliSystem (system="sys", info="System commands")
public class SystemCommands {
    
    public SystemCommands(){
        
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
            java.io.InputStream is = p.getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
            // And print each line
            String s = null;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemCommands.class.getName()).log(Level.SEVERE, null, ex);
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
            java.io.InputStream is = p.getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
            // And print each line
            String s = null;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            is.close();
        } catch (IOException ex) {
            Logger.getLogger(SystemCommands.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SystemCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("done...");
    }
        
    
    public static void main(String[] args){
        SystemCommands command = new SystemCommands();
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
