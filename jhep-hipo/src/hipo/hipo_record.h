/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   hipo_record.h
 * Author: gavalian
 *
 * Created on March 9, 2017, 1:25 PM
 */

#ifndef HIPO_RECORD_H
#define HIPO_RECORD_H

#ifdef __cplusplus
extern "C" {
#endif

#include "data_utils.h"
#include "hipo_file.h"
    
    typedef struct {
        int signatureString; // 1) identifier string is HREC (int = 0x43455248 
        int recordLength; // 2) TOTAL Length of the RECORD, includes INDEX array
        int recordDataLength; // 3) Length of the DATA uncompressed
        int recordDataLengthCompressed; // 4) compressed length of the DATA buffer
        int numberOfEvents ; // 5) number of event, data buckets in DATA buffer
        int headerLength ; // 6) Length of the buffer represengin HEADER for the record
        int indexDataLength ; // 7) Length of the index buffer (in bytes)
        int compressionType;
    } record_header_t ;

    typedef struct {
        record_header_t header;
        data_buffer     index;
        data_buffer     data;
    } hipo_record_t ;

    typedef struct {
        data_buffer  index;       
    } record_index_t ;
    
    typedef struct {        
        data_buffer data;
    } hipo_event_t ;
    
    
    void read_record_event(hipo_record_t *record, hipo_event_t *event, int order);
    int  read_record_header(FILE *fp, record_header_t *header, unsigned long position);
    int  read_record(FILE *fp, hipo_record_t *record, int position);
    void read_record_data(hipo_file_t *file,record_index_t *rindex, hipo_record_t *record,int record_id);
    
    void read_record_index(hipo_file_t *file, record_index_t *index);
    void print_record_header(record_header_t header);
    void print_record(hipo_record_t record);
    
    void uncompress_LZ4(data_buffer *outbuffer, char* buffer, int bufferLength, int decompressedLength);

    
#ifdef __cplusplus
}
#endif

#endif /* HIPO_RECORD_H */

