/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.cli.test;

import org.jlab.jhep.cli.base.CliCommand;
import org.jlab.jhep.cli.base.CliSystem;

/**
 *
 * @author gavalian
 */
@CliSystem (system="string", info="string manipulation commands")
public class StringCommands {
    public StringCommands(){
        
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
    
    @CliCommand(
            command="toupper",
            info="converts string to lower case",
            defaults={"a"},
            descriptions={"string to convert to lower case"}
    )
    public void tolower(String input){
        String output = input.toUpperCase();
        System.out.println(output);
    }
    
}
