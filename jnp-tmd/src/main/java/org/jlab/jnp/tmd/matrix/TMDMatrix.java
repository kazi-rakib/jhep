/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.tmd.matrix;

import org.jlab.jnp.math.matrix.SparseMatrix;
import org.jlab.jnp.math.matrix.SparseMatrixReader;
import org.jlab.jnp.utils.options.OptionParser;


/**
 *
 * @author gavalian
 */
public class TMDMatrix {
    
    public static void importMatrix(String jsonFile, String hipoFile){
        SparseMatrix matrix = SparseMatrixReader.readTextMatrix(jsonFile);
        SparseMatrixReader.writeSparseMatrix(matrix, hipoFile);
    }
    
    public static void main(String[] args){
        OptionParser parser = new OptionParser();
        parser.addOption("-import", "0");
        parser.addOption("-show", "0");
        parser.addOption("-print", "0");
        parser.addOption("-o", "0");
        parser.parse(args);
        
        if(parser.getOption("-import").stringValue().compareTo("0")!=0){
            String inputFile = parser.getOption("-import").stringValue();
            String outputFile = parser.getOption("-o").stringValue();
            TMDMatrix.importMatrix(inputFile, outputFile);
            return;
        }
        
        if(parser.getOption("-show").stringValue().compareTo("0")!=0){
            String inputFile = parser.getOption("-show").stringValue();
            SparseMatrix matrix = SparseMatrixReader.readSparseMatrix(inputFile);
            matrix.show();
            return;
        }
        
        if(parser.getOption("-print").stringValue().compareTo("0")!=0){
            String inputFile = parser.getOption("-print").stringValue();
            SparseMatrix matrix = SparseMatrixReader.readSparseMatrix(inputFile);
            System.out.println(matrix.contentString());
            return;
        }
        
        parser.printUsage();
    }
}
