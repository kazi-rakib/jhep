/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   reader_test.c
 * Author: gavalian
 *
 * Created on March 9, 2017, 3:38 PM
 */

#include <stdio.h>
#include <stdlib.h>
#include "hipo_file.h"
#include "hipo_record.h"
#include "data_utils.h"
#include "hipo_utils.h"
/*
 * 
 */
int main(int argc, char** argv) {
    printf("testing hipo file reading\n");
    if(argc>1){
        char filename[128];
        sprintf(filename,"%s",argv[1]);
        printf("---> opening file : %s\n",filename);
        
        openFile(filename);
        
        int nrecords = getEntries();
        int i;
        int e;
        for( i = 0; i < nrecords; i++){
            readRecord(i);
            printf("reading record # %d\n",i);
            int nevents = getEvents();
            for( e = 0; e < nevents; e++){
                readEvent(e);
                readNode(22,1);
                //printf("size = %d\n",hipo_node.size);
            }
            //printf("record = %12d  # events = %d\n" , i,nevents);
        }
        /*hipo_file_t hipoFile = open_hipo_file(filename);
        print_file_info(hipoFile);
        record_index_t record_index;
        
        read_record_index(&hipoFile,&record_index);
        data_show(&record_index.index);
        
        hipo_record_t record;
        for(int i = 3 ; i < 9; i++){
            read_record_data(&hipoFile,&record_index, &record,i);
        }*/
        /*int position = hipoFile.firstRecordPosition;
        int counter  = 0;
        while(position+72<hipoFile.fileSize){
            printf("------- %d  counter = %d\n",position, counter);
            record_header_t  record_header;
            read_record_header(hipoFile.fp,&record_header, position);
            print_record_header(record_header);
            position += record_header.recordLength;
            counter++;
        }*/
    }
    
    data_buffer buffer;
    data_allocate(&buffer,128);
    data_write_int(&buffer,2,42567);
    
    int value = data_read_int(&buffer,2);
    
    printf ("value = %d  %d\n",value, data_read_int(&buffer,0));
    
    data_show(&buffer);
    
    data_expand(&buffer,24);
    data_show(&buffer);
    value = data_read_int(&buffer,2);
    printf ("value = %d  %d\n",value, data_read_int(&buffer,0));
    data_free(&buffer);
    data_show(&buffer);
    return (EXIT_SUCCESS);
}

