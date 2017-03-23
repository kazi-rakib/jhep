/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "hipo_utils.h"
#include <string.h>

hipo_file_t    hipo_file;
record_index_t hipo_file_index;
hipo_record_t  hipo_record;
hipo_event_t   hipo_event;
hipo_node_t    hipo_node;



void  openFile(const char *filename){
    
    hipo_file = open_hipo_file(filename);
    read_record_index(&hipo_file,&hipo_file_index);
    
    printf("[OPEN]    FILE : %s\n",filename);
    printf("[OPEN] RECORDS : %d\n", getEntries());
    
    hipo_record.index.size = 0;
    hipo_record.index.buffer = NULL;
    hipo_record.data.size = 0;
    hipo_record.data.buffer = NULL;
    data_allocate(&hipo_event.data,20);
    data_allocate(&hipo_node.data,20);
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
    //hipo_event_t hipo_event;
    //hipo_node_t  hipo_node;
    data_free(&hipo_event.data);
    read_record_event(&hipo_record,&hipo_event,event);
    //printf("---> event\n");
    //data_print(&hipo_event.data,20,1000);
    //read_event_node(22,1,&hipo_event,&hipo_node);
    /*if(hipo_node.size>0){
        printf("size = %d,  value = %d \n", hipo_node.size,get_node_value_int(&hipo_node,0));
    }*/
    //data_free(&hipo_event.data);
    return 1;
}

void readNode(int group, int item){
    data_free(&hipo_node.data);
    read_event_node(group,item,&hipo_event,&hipo_node);
    //printf("size = %d\n",hipo_node.size);
}

//void read_int(int *)
/**
 * This is the part that interfaces with FORTRAN
 * @param nrecords
 * @param filename
 * @param len
 */
void open_hipo_(int *nrecords, const char *filename, int len){
    char *buffer = malloc(len+1);
    memcpy(buffer,filename,len);
    buffer[len] = '\0';
    printf("FORTRAN opening file : %s\n", buffer);
    openFile(buffer);
    *nrecords = getEntries();
    free(buffer);
}

void close_hipo_(){
    fclose(hipo_file.fp);
    hipo_file.fileSize = 0;
    hipo_file.firstRecordPosition = 0;
    hipo_file.headerRecordPosition = 0;
}

void read_record_(int *record, int *nevents){
    int r = *record;
    readRecord(r);
    int ev = getEvents();
    *nevents = ev;
}

void read_event_(int *event){
    readEvent(*event);
}


void read_node_float_(int *group, int *item, int *nread, float *buffer){
    int c_group = *group;
    int  c_item = *item;
    //printf("testing\n");
    hipo_node_t node;
    //printf("looking for %d %d %d \n",c_group,c_item, *nread);
    read_event_node(c_group,c_item,&hipo_event,&node);
    
    if(node.size==0){
        *nread = 0;
        return;
    }
    if(node.type==4){
        int counter = 0;
        int    iter = node.size;
        //if(iter>*maxLength) iter = *maxLength;
        for(int i = 0; i < iter; i++){
            float value = get_node_value_float(&node,i);
            buffer[i] = value;
            counter++;
        }
        *nread = counter;
        return;
    }
}

void read_node_int_(int *group, int *item, int *nread, int *buffer){
    int c_group = *group;
    int  c_item = *item;
    //printf("testing\n");
    hipo_node_t node;
    //printf("looking for %d %d %d \n",c_group,c_item, *nread);
    read_event_node(c_group,c_item,&hipo_event,&node);
    
    //return;
    
    if(node.size==0){
        *nread = 0;
        return;
    }
    
    //printf("size = %d\n",node.size);
    //return;
    
    if(node.type==1){
        int counter = 0;
        int    iter = node.size;
        //if(iter>*maxLength) iter = *maxLength;
        for(int i = 0; i < iter; i++){
            int value = (uint8_t) get_node_value_byte(&node,i);
            buffer[i] = value;
            counter++;
        }
        *nread = counter;
        data_free(&node.data);
        return;
    }
    
      if(node.type==2){
        int counter = 0;
        int    iter = node.size;
        //if(iter>*maxLength) iter = *maxLength;
        for(int i = 0; i < iter; i++){
            int value = get_node_value_short(&node,i);
            buffer[i] = value;
            counter++;
        }
        *nread = counter;
        data_free(&node.data);
        return;
    }
    
    
    //printf("found one size = %d\n",node.size);
    if(node.type==3){
        int counter = 0;
        int    iter = node.size;
        //if(iter>*maxLength) iter = *maxLength;
        for(int i = 0; i < iter; i++){
            int value = get_node_value_int(&node,i);
            buffer[i] = value;
            counter++;
        }
        *nread = counter;
        data_free(&node.data);
        return;
    }
      
}
