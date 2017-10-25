/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.math.cli;

import java.io.IOException;
import org.jlab.jnp.cli.main.CliLogo;
import org.jlab.jnp.cli.main.CliModuleManager;
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
public class MathCli {
     public static void main(String[] args) throws IOException {
        //ConsoleReader reader = new ConsoleReader();
        CliLogo.printLogo();
        
        CliModuleManager cliMain = new CliModuleManager();
        cliMain.initModule("org.jlab.jhep.cli.test.SystemCommands");
        cliMain.initModule("org.jlab.jhep.math.cli.MatrixCli");
        
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
