/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.cli.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 *
 * @author gavalian
 */
public class CommandClassParser {
    
    public CommandClassParser(){
        
    }
    
    public void parse(Class clazz){
        Method[] methods = clazz.getMethods();
        for(Method method : methods){
            Type[] types = method.getParameterTypes();
            Annotation[] annotations = method.getDeclaredAnnotations();
            System.out.println("----> method : " + method.getName() 
                    + " >>> size = " + annotations.length + 
                    "  " + method.isAnnotationPresent(CliCommand.class));
            for(Type type : types){
                System.out.println("\t\t---> " + type);
            }
            for(Annotation ann : annotations){
                System.out.println(ann.toString());
            }
        }
    }
}
