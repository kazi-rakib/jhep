/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.utils.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gavalian
 */
public class ArrayUtils {
    
    public static String getBracketString(String operator, int skip){
        
        Pattern regex = Pattern.compile("\\[(.*?)\\]");
        Matcher regexMatcher = regex.matcher(operator);
        int counter = 0;
        while(regexMatcher.find()){
            //System.out.println(regexMatcher.group(1));
            if(counter==skip) return regexMatcher.group(1);
            counter++;
        }
        
        return null;
    }
    
    public static String getBracketStringCurly(String operator, int skip){
        
        Pattern regex = Pattern.compile("\\{(.*?)\\}");
        Matcher regexMatcher = regex.matcher(operator);
        int counter = 0;
        while(regexMatcher.find()){
            //System.out.println(regexMatcher.group(1));
            if(counter==skip) return regexMatcher.group(1);
            counter++;
        }
        
        return null;
    }
    
    public static List<String> getArray(String array){
        String[] tokens = array.split(",");
        List<String>  list = new ArrayList<String>();
        for(int i = 0; i < tokens.length; i++){
            list.add(tokens[i].trim());
        }
        return list;
    }
    
    public static String getString(double[] array, String format, String separator){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++){
           if(i!=0) str.append(separator);
           str.append(String.format(format, array[i]));
        }
        return str.toString();
    }
    
    public static String getString(double[] array, String separator){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++){
           if(i!=0) str.append(separator);
           str.append(array[i]);
        }
        return str.toString();
    }
    
    
    public static String getMapString(Map<String,Double> map, String format, String separator){
         StringBuilder str = new StringBuilder();
         int i = 0;
         for(Map.Entry<String,Double> entry : map.entrySet()){
             if(i!=0) str.append(separator);
             str.append(String.format(format,entry.getValue()));
             i++;
         }
         return str.toString();
    }
    
    public static String getMapStringWithKey(Map<String,Double> map, String format, String separator){
         StringBuilder str = new StringBuilder();
         int i = 0;
         for(Map.Entry<String,Double> entry : map.entrySet()){
             if(i!=0) str.append(separator);
             str.append(entry.getKey()).append(" = ");
             str.append(String.format(format,entry.getValue()));
             i++;
         }
         return str.toString();
    }
    
    public static void main(String[] args){
        String schema = "{1302,DC::true}[1,px,FLOAT][2,py,FLOAT][3,pz,FLOAT]";
        String result = ArrayUtils.getBracketString(schema,0);
        String resultcurly = ArrayUtils.getBracketStringCurly(schema,0);
        System.out.println( "result = " + result);
        System.out.println( "result = " + resultcurly);
    }
}
