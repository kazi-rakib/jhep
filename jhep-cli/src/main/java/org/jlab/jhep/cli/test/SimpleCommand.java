/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.cli.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jhep.cli.base.CliCommand;
import org.jlab.jhep.cli.base.CommandClassParser;

/**
 *
 * @author gavalian
 */
public class SimpleCommand {
    
    public SimpleCommand(){
        
    }
    
    @CliCommand(
            command="ls", 
            defaults={"a","b"}, 
            descriptions={"value a","value b"}
    )
    public void ls(){
        try {
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec("ls");
        } catch (IOException ex) {
            Logger.getLogger(SimpleCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @CliCommand(
            command="pwd", 
            defaults={"a","b"},            
            descriptions={"value a","value b"}
    )
    public void pwd(){
        try {
            java.lang.Runtime rt = java.lang.Runtime.getRuntime();
            // Start a new process: UNIX command ls
            java.lang.Process p = rt.exec("pwd");
        } catch (IOException ex) {
            Logger.getLogger(SimpleCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public static void main(String[] args){
        SimpleCommand command = new SimpleCommand();
        CommandClassParser parser = new CommandClassParser();
        parser.parse(command.getClass());
    }
}
