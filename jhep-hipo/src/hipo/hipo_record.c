/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
#include <stdio.h>
#include "hipo_record.h"
#include <string.h>
#ifdef __LZ4__
#include "lz4.h"
#endif


/**
 * Reads record header from given position in the file. After reading
 * it validates header by checking first 4 bytes to match with HREC.
 * @param fp file descriptor 
 * @param header header class
 * @param position position in file
 * @return 
 */
int read_record_header(FILE *fp, record_header_t *header, unsigned long position){
    fseek(fp,position,SEEK_SET);
    fread(header,sizeof(*header),1,fp);
    
    return -1;
}

int read_record(FILE *fp, hipo_record_t *record, int position){
    fseek(fp,position,SEEK_SET);
    fread((&record->header),sizeof(record->header),1,fp);
    return -1;
}

void print_record_header(record_header_t header){
    printf("%18s : %X\n", "identifier",(unsigned int) header.signatureString);
    printf("%18s : %d\n", "length", (unsigned int) header.recordLength);
    printf("%18s : %d\n", "data length",(unsigned int) header.recordDataLength);
    printf("%18s : %d\n", "data length c", (unsigned int) header.recordDataLengthCompressed);
    printf("%18s : %d\n", "events", (unsigned int) header.numberOfEvents);
    printf("%18s : %d\n", "header length", (unsigned int) header.headerLength);
    printf("%18s : %d\n", "index buffer", (unsigned int) header.indexDataLength);

}

void read_record_index(hipo_file_t *file, record_index_t *index){
    data_allocate(&index->index,0);
    unsigned long position = file->firstRecordPosition;
    record_header_t  record_header;
    int     counter = 0;
    while(position + 40 < file->fileSize){
        read_record_header(file->fp,&record_header, position);
        data_expand(&index->index, 8+4+4);
        data_write_long(&index->index, counter*16    , position);
        data_write_int (&index->index, counter*16+8  , record_header.recordLength);
        data_write_int (&index->index, counter*16+8+4, record_header.numberOfEvents);
        
        position += record_header.recordLength;
        counter++;
    }
}

void read_record_data(hipo_file_t *file,record_index_t *rindex, hipo_record_t *record,int record_id){
    
    unsigned long offset  = data_read_long(&rindex->index, record_id*16);
    int           length  = data_read_int(&rindex->index,record_id*16+8);
    int           nevents = data_read_int(&rindex->index,record_id*16+8+4);
    
    //printf("------> RECORD in position %lu\n\n",offset);
    record_header_t record_header;

    //print_record_header(record_header);

    read_record_header(file->fp,&record_header, offset);
    record_header.recordDataLengthCompressed = (record_header.recordDataLengthCompressed&0x00FFFFFF);    
    int dataLength    = (record_header.recordDataLengthCompressed&0x00FFFFFF);
    int dataLengthUnc = record_header.recordDataLength;
    int indexLength   = record_header.indexDataLength;
    int headerLength  = record_header.headerLength;
    
    //printf("********** READING DATA SIZE %d\n",dataLength);
    
    //print_record_header(record_header);
    
    data_allocate(&record->index,indexLength);
    fseek(file->fp, offset + 40 + headerLength,SEEK_SET);
    fread(record->index.buffer, indexLength ,1,file->fp);
    
    data_allocate(&record->data,dataLengthUnc);
    
    data_buffer raw;
    data_allocate(&raw,dataLength);
    
    //printf("read data at %6d  OFFSET %9lu\n", 40 + headerLength + indexLength, offset);
    unsigned long dataOffset = 40 + headerLength + indexLength + offset;
    fseek(file->fp, offset + 40 + headerLength + indexLength, SEEK_SET);
    
    fread(raw.buffer, dataLength, 1, file->fp);
    //printf("done reading data");
    uncompress_LZ4(&record->data,raw.buffer, dataLength, dataLengthUnc);
    //data_print(&raw,20,1000);
    data_free(&raw);
    //record_header_t 
    //char     *record_data = malloc(length);
    //printf("****************************\n");
    //printf(" record no %d\n\n",record_id);
    //data_show(&record->index);
    //data_show(&record->data);
    //printf("****************************\n");
}

void print_record(hipo_record_t record){
    
}


void read_record_event(hipo_record_t *record, hipo_event_t *event, int order){
    int offset = 0;
    int length = 0;
    for(int i = 0; i < order; i++){
        int el = data_read_int(&record->index,i*4);
        offset += el;
    }
    length  = data_read_int(&record->index,order*4);
    //printf("ORDER = %d, OFFSET = %d, LENGTH = %d\n",order,offset,length);
    data_allocate(&event->data,length);
    char* ptr = (char *) record->data.buffer;
    memcpy( (char *) event->data.buffer, &ptr[offset], length);
}

void uncompress_LZ4(data_buffer *outbuffer, char* buffer, int bufferLength, int decompressedLength){
#ifdef __LZ4__
    //printf("decompressing -> %d  %d\n",bufferLength,decompressedLength);
    //    char *destUnCompressed = new char[decompressedLength];
    //printf("deompressing\n");
    int result = LZ4_decompress_safe(buffer,outbuffer->buffer,bufferLength,decompressedLength);  
    //printf(" FIRST (%d) = %x %x %x %x\n",result);//,destUnCompressed[0],destUnCompressed[1],
            //destUnCompressed[2],destUnCompressed[3]);  
    //LZ4_decompress_fast(buffer,destUnCompressed,decompressedLength);  
    //LZ4_uncompress(buffer,destUnCompressed,decompressedLength);
#endif

#ifndef __LZ4__
  printf("\n   >>>>> LZ4 compression is not supported.");
  printf("\n   >>>>> check if libz4 is installed on your system.");  
  printf("\n   >>>>> recompile the library with liblz4 installed.\n");  
#endif  

}
