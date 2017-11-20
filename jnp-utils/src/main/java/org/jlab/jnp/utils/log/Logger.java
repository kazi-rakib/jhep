/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.utils.log;

/**
 *
 * @author gavalian
 */
public class Logger {
    
    private boolean loggingDebug = false;
    private boolean loggingError = true;
    private boolean  loggingInfo = true;
    private String     className = "undefined";
    private String      redColor = "[91m";
    private String      greenColor = "[92m";
    private String      yellowColor = "[93m";
    private String      resetColor  = "[0m";
    
    public Logger(){
        
    }
    
    public Logger setClassName(String name){ className = name; return this;}
    public Logger setDebug(boolean flag) { loggingDebug = flag; return this;}
    public Logger setError(boolean flag) { loggingDebug = flag; return this;}
    public Logger setInfo( boolean flag) {  loggingInfo = flag; return this;}
    
    public void info(String log){
        if(loggingInfo==true){
            System.out.println(String.format("[%s%s%s] >> %s", greenColor,className, resetColor, log));
        }
    }
    
    public void debug(String log){
        if(loggingDebug==true){
            System.out.println(String.format("[%c%s%s (debug) : %c%s] >> %s",(char) 27, 
                    yellowColor, className, (char) 27,  resetColor,log));
        }
    }
    
    public void error(String log){
        if(loggingError==true){
            System.out.println(String.format("[%c%s%s (error) : %c%s] >> %s", (char) 27,
                    redColor,className,(char) 27,resetColor, log));
        }
    }
    
    public static void main(String[] args){
        Logger log = new Logger();
        log.info("Testing the logger");
        
    }
}
