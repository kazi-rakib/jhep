/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   hipofile.h
 * Author: gavalian
 *
 * Created on March 9, 2017, 3:25 PM
 */

#ifndef HIPOFILE_H
#define HIPOFILE_H
#include <stdio.h>
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 72 bytes is the length of the HIPO file header. includes signature 
 * string version string.
 */
#define HIPO_FILE_HEADER_SIZE 72; 
   

typedef struct {
  int signatureString;
  int versionString;
  int uniqueID;
  int fileHeaderLength;
  int fileType;
} hipo_file_header_t;


typedef struct {
    FILE *fp;
    
    hipo_file_header_t  header;
    unsigned long  fileSize;
    int   headerRecordPosition;
    int   firstRecordPosition;
} hipo_file_t ;


hipo_file_t open_hipo_file(const char *filename);
void        print_file_info(hipo_file_t hipo_file);

#ifdef __cplusplus
}
#endif

#endif /* HIPOFILE_H */

