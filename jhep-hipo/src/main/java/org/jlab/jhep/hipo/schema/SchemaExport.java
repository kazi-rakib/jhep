/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.hipo.schema;

import java.util.List;
import org.jlab.jhep.hipo.data.HipoNodeType;

/**
 *
 * @author gavalian
 */
public class SchemaExport {
    
    static String INDENT = "      ";
    static String CONTINUE = "     + ";    
    
    public static String createCommonBlock(Schema schema){
        //int nentries = schema.getEntries();
        StringBuilder str = new StringBuilder();
        List<String> schemaEntries = schema.schemaEntryList();
        int nentries = schemaEntries.size();
        
        for(int i = 0; i < nentries; i++){
            HipoNodeType type = schema.getEntry(schemaEntries.get(i)).getType();
            String ftype = "REAL";
            switch(type){
                case INT: ftype = "INTEGER"; break;
                case FLOAT: ftype = "REAL"; break;
                default: ftype = "REAL";
            }
            
            str.append(SchemaExport.INDENT).append(ftype).append(" ")
                    .append(schema.getEntry(schemaEntries.get(i)).getName()).append("\n");
        }
        
        str.append(SchemaExport.INDENT).append("COMMON/").append(schema.getName().replaceAll("::", "_")).append("/");
        str.append(schema.getName().replaceAll("::", "_")).append("_id,");
        int counter = 0;
        for(int i = 0; i < nentries; i++){
            if(i!=0) str.append(",");
            counter += schema.getEntry(schemaEntries.get(i)).getName().length();
            str.append(schema.getEntry(schemaEntries.get(i)).getName());
            if(counter>56){
                str.append("\n").append(SchemaExport.CONTINUE);
                counter = 0;
            }
        }
        return str.toString();
    }
    
    public static String createBookString(Schema schema){
        
        StringBuilder format = new StringBuilder();
        String id_name = schema.getName().replace("::", "_") + "_id";
        format.append(id_name).append("[0,400]:I");
        int nentries = schema.getEntries();
        
        for(int i = 0; i < nentries; i++){
            
        }
        return format.toString();
    }
    
    public static String createFactoryString(SchemaFactory factory){
        List<Schema> schemas = factory.getSchemaList();
        StringBuilder str = new StringBuilder();
        for(Schema schema : schemas){
            str.append(SchemaExport.createCommonBlock(schema)).append("\n");
        }
        return str.toString();
    }
    public static void main(String[] args){
        SchemaFactory factory = new SchemaFactory();
        factory.initFromDirectory("/Users/gavalian/Work/Software/Release-4a.0.0/COATJAVA/coatjava/etc/bankdefs/hipo");
        factory.show();
        System.out.println(  "RESULT = " +  factory.hasSchema("ECAL::adc"));
        Schema schema = factory.getSchema("ECAL::adc");
        
        System.out.println(SchemaExport.createFactoryString(factory));
    }
}
