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
@CliSystem (system="math", info="string manipulation commands")
public class MathCommands {
    public MathCommands(){
        
    }
    
    @CliCommand(
            command="add",
            info="adds two numbers",
            defaults={"1","1"},
            descriptions={"first number", "second number" }
    )
    public void add(int i1, int i2){        
        int summ = i1 + i2;
        System.out.println(i1 + " + " + i2 + " = " + summ);
    }
    
    @CliCommand(
            command="mult",
            info="multiplies two numbers",
            defaults={"1","1"},
            descriptions={"first number", "second number" }
    )
    public void mult(int i1, int i2){        
        int summ = i1 * i2;
        System.out.println(i1 + " * " + i2 + " = " + summ);
    }
    
    @CliCommand(
            command="div",
            info="divides two numbers",
            defaults={"1","1"},
            descriptions={"first number", "second number" }
    )
    public void div(int i1, int i2){ 
        int summ = i1 / i2;
        System.out.println(i1 + " / " + i2 + " = " + summ);
    }
    
    @CliCommand(
            command="mod",
            info="calculated remainder of two numbers",
            defaults={"1","1"},
            descriptions={"first number", "second number" }
    )
    public void mod(int i1, int i2){ 
        int summ = i1 % i2;
        System.out.println(i1 + " mod " + i2 + " = " + summ);
    }
}
