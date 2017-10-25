/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author gavalian
 */
public class HipoFileInfo extends AbstractTableModel {

    List<HipoRecordHeader>  fileRecords = new ArrayList<HipoRecordHeader>();
        
    private int numberOfEvents = 0;
    private int currentRecord  = 0;
    private int currentEvent   = 0;
    private int currentRecordLastEvent = 0;
    private int currentRecordFirstEvent = 0;
    
    public HipoFileInfo(){
        
    }
    
    public void addRecord(HipoRecordHeader header){
        this.fileRecords.add(header);
    }
    
    public void clear(){
        this.fileRecords.clear();
        this.currentEvent =0;
        this.currentRecord = 0;
        this.currentRecordFirstEvent =0;
        this.currentRecordLastEvent =0;
    }
    
    public void reset(){
        currentEvent  = 0;
        currentRecord = 0;
        currentRecordLastEvent = 0;
        currentRecordFirstEvent = 0;
        numberOfEvents = 0;
        if(fileRecords.size()>0){
            currentRecordLastEvent = fileRecords.get(0).getNumberOfEvents()-1;
            for(int i = 0; i < fileRecords.size(); i++){
                numberOfEvents += fileRecords.get(i).getNumberOfEvents();
            }
        }                
    }
    
    private void incrementRecord(){
        currentRecord++;
        this.currentRecordFirstEvent = this.currentRecordLastEvent+1;
        this.currentRecordLastEvent  = 
                this.currentRecordFirstEvent + 
                fileRecords.get(currentRecord).getNumberOfEvents()-1;
    }
    
    public void setEvent(int index){
        if(index>currentEvent){
            if(index <= currentRecordLastEvent){
                currentEvent = index;
            } else {
                currentEvent = index;
                while(currentEvent>currentRecordLastEvent){
                    this.incrementRecord();
                }
            }
        }
    }
    
    public int getRecord(){
        return this.currentRecord;
    }
    
    public int getEventOffset(){
        return (currentEvent - currentRecordFirstEvent);
    }
    
    

    @Override
    public int getRowCount() {
        return this.fileRecords.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
         switch(columnIndex){
            case 0: Integer rowNum = rowIndex; return rowNum.toString();
            //case 1: Long       pos = fileRecords.get(rowIndex).getPosition(); return pos.toString();
            //case 2: Integer     = fileRecords.get(rowIndex).getPosition(); return pos.toString();
            default : return "N/A";
        }

    }
    
    public static void main(String[] args){
        String filename = "/Users/gavalian/Work/Software/Release-4a.0/COATJAVA/coatjava/clas12dst_000809.hipo";
        //HipoBenchmark.recordReadingBenchmark(filename);
        //HipoBenchmark.eventReadingBenchmark(filename);
        HipoReader reader = new HipoReader();
        reader.open(filename);
        int nevents = reader.getEventCount();
        System.out.println(" N# = " + nevents);
        for(int i = 42000000; i < nevents; i++){
            reader.getFileInfo().setEvent(i);
            System.out.println("  I =  " + i 
                    + "  RECORD = " + reader.getFileInfo().getRecord()
                    + " OFFSET = " + reader.getFileInfo().getEventOffset());
        }
        
    }
}
