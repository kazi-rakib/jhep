/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jhep.math.cli;

import org.jlab.jhep.cli.base.CliCommand;
import org.jlab.jhep.cli.base.CliSystem;
import org.jlab.jhep.math.matrix.MatrixStore;
import org.jlab.jhep.math.matrix.SparseMatrixReader;

/**
 *
 * @author gavalian
 */
@CliSystem (system="matrix", info="grid matrix manipulation library")
public class MatrixCli {
    
    private final MatrixStore store = new MatrixStore();
    
    @CliCommand(
            command="export",
            info="reads matrix from text file and converts into data GRID (HIPO)",
            defaults={"input.txt","output.hipo"},
            descriptions={"input text file", "output HIPO file name" }
    )
     
    public void export(String inputText, String outputHipo){
        System.out.println("exporting : " + inputText + " ==> " + outputHipo);
        SparseMatrixReader.exportMatrix(inputText, outputHipo);
    }
    
    @CliCommand(
            command="open",
            info="reads matrix from HIPO file.",
            defaults={"intput.hipo"},
            descriptions={"the hipo file name containing grid" }
    )
    public void open(String inputHipo){
        System.out.println("opening file : " + inputHipo);
        store.readMatrix(inputHipo);
    }
    
    @CliCommand(
            command="list",
            info="prints list of loaded matricies",
            defaults={},
            descriptions={}
    )
    public void list(){
        store.show();
    }
}
