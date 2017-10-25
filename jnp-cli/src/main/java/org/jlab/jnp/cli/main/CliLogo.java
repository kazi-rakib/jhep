/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.cli.main;

/**
 *
 * @author gavalian
 */
public class CliLogo {
    public static String[] cliLogo(){
        String[] logo = new String[]{
            "*********************************************************",
            "* JHEP CLI (interactive)                                *",
            "*                                   >=<                 *", 
            "* Version : 1.0                ,.--'  ''-.              *", 
            "*  Author : G.G.               (  )  ',_.'  Powered By: *", 
            "*    Date : 2/14/2017           Xx'xX          HIPO 2.0 *",
            "*********************************************************"  
        };
        return logo;
    }
    
    public static void printLogo(){
        String[] logoLines = CliLogo.cliLogo();
        System.out.println("\n");
        for(String line : logoLines){
            System.out.println(line);
        }
        System.out.println();
    }
}
