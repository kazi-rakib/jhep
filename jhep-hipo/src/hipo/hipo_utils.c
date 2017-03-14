/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "hipo_utils.h"

hipo_file_t    hipo_file;
record_index_t hipo_file_index;
hipo_record_t  hipo_record;



void  openFile(const char *filename){
    
    hipo_file = open_hipo_file(filename);
    read_record_index(&hipo_file,&hipo_file_index);
    
    printf("[OPEN]    FILE : %s\n",filename);
    printf("[OPEN] RECORDS : %d\n", getEntries());
    
    hipo_record.index.size = 0;
    hipo_record.index.buffer = NULL;
    hipo_record.data.size = 0;
    hipo_record.data.buffer = NULL;
}

int   getEntries(){
    int nrecords = hipo_file_index.index.size;
    nrecords /= 16;
    return nrecords;
}

int   readRecord(int record){
    data_free(&hipo_record.data);
    data_free(&hipo_record.index);
    read_record_data(&hipo_file,&hipo_file_index, &hipo_record,record);
    return 1;
}

int   getEvents(){
    int nevents = hipo_record.index.size/4;
    return nevents;
}

int   readEvent(int event){ 
    hipo_event_t hipo_event;
    read_record_event(&hipo_record,&hipo_event,event);
    printf("---> event\n");
    data_print(&hipo_event.data,20,1000);
    data_free(&hipo_event.data);
    return 1;
}