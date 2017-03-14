/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
#include <stdio.h>
#include "hipo_file.h"

hipo_file_t open_hipo_file(const char *filename){
    hipo_file_t  hipoFile;
    hipoFile.fp = fopen(filename,"r");
    fread(&hipoFile.header,sizeof(hipoFile.header),1,hipoFile.fp);
    hipoFile.headerRecordPosition = HIPO_FILE_HEADER_SIZE;
    hipoFile.firstRecordPosition  = hipoFile.header.fileHeaderLength + HIPO_FILE_HEADER_SIZE;
    
    fseek(hipoFile.fp,0,SEEK_END);
    hipoFile.fileSize = (unsigned long) ftell(hipoFile.fp);
    fseek(hipoFile.fp,0,SEEK_SET);
    return hipoFile;
}

void  print_file_info(hipo_file_t hipo_file){
    
    printf("%18s : %X\n", "identifier"    , (unsigned int) hipo_file.header.signatureString);
    printf("%18s : %X\n", "version"       , (unsigned int) hipo_file.header.versionString);
    printf("%18s : %lu\n", "file size"     , hipo_file.fileSize);
    printf("%18s : %d\n", "header at"     , hipo_file.headerRecordPosition);
    printf("%18s : %d\n", "header length" , hipo_file.header.fileHeaderLength);
    printf("%18s : %d\n", "record at"     , hipo_file.firstRecordPosition);
    
}
