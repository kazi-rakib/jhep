/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.cli.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jhep.cli.base.CliClass;
import org.jline.builtins.Completers.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 * @author gavalian
 */
public class CliMain {
    
    List<String> completerCommands = new ArrayList<String>();
    Map<String,CliClass>  commands = new LinkedHashMap<String,CliClass>();
    
    public CliMain(){
        
    }
    
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
            Logger.getLogger(CliMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public StringsCompleter getCompleter(){
        String[] args = new String[this.completerCommands.size()];
        for(int i = 0; i < args.length; i++){
            args[i] = this.completerCommands.get(i);
        }
        return new StringsCompleter(args);
    }
    
    public void printHelp(){
        for(Map.Entry<String,CliClass> entry : this.commands.entrySet()){
            System.out.println(String.format( " %14s : %s",
                    entry.getValue().getSystemName(), entry.getValue().getSystemInfo()
                    ));
        }
    }
    
    public void printSystemHelp(String system){
        this.commands.get(system).help();
    }
    
    public void printSystemCommandHelp(String system, String command){
        
    }
    
    public void printMessageUnrecognizedCommand(String command){
        System.out.println("\033[33m warning\033[0m: unrecognized command \"" + command + "\"");
    }
    
    public static void main(String[] args) throws IOException {
        //ConsoleReader reader = new ConsoleReader();
        CliLogo.printLogo();
        
        CliModuleManager cliMain = new CliModuleManager();
        cliMain.initModule("org.jlab.jhep.cli.test.SystemCommands");
        cliMain.initModule("org.jlab.jhep.cli.test.MathCommands");
        cliMain.initModule("org.jlab.jhep.cli.test.StringCommands");
        
        System.out.println("\n");
        String prompt = "jhep-cli> ";
            String rightPrompt = null;
        TerminalBuilder builder = TerminalBuilder.builder();
        StringsCompleter completer = cliMain.getCompleter();//new StringsCompleter("hist/read", "hist/plot", "hist/show");
        Parser parser = new DefaultParser();
        Terminal terminal = builder.build();
        
        LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .parser(parser)
                    .build();
        
        while (true) {
            
                String line = null;
                try {
                    line = reader.readLine(prompt, rightPrompt, null, null);
                } catch (UserInterruptException e) {
                    // Ignore
                } catch (EndOfFileException e) {
                    return;
                }
                if (line == null) {
                    continue;
                }

                line = line.trim();
                
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit") || 
                        line.equalsIgnoreCase("bye")) {
                    break;
                }
                
                if(line.equalsIgnoreCase("help")==true){
                    //cliMain.printHelp();
                }
                
                if(line.startsWith("help")==true){
                    String[] tokens = line.split("\\s+");
                    if(tokens.length>1){
                        //cliMain.printSystemHelp(tokens[1]);
                    }
                }
                cliMain.execute(line);
                //System.out.println(line);
                //cliMain.printMessageUnrecognizedCommand(line);
        }
        System.out.println("\n Bye-bye...\n");
    }
}
