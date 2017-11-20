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
public class LoggerFactory {
    
    public static Logger getLogger(Class clazz){
       Logger logger = new Logger();
       logger.setClassName(clazz.getSimpleName());
       logger.setInfo(true).setError(true).setDebug(false);
       //logger.setDebug(true);
       return logger;
    }
}
