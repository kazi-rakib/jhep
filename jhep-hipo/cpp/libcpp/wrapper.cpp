#include <iostream>
#include "reader.h"

hipo::reader hipoReader;

extern "C" {

  void hipo_open_file_(int *nrecords, const char *filename, int length){
    char *buffer = (char *) malloc(length+1);
    memcpy(buffer,filename,length);
    buffer[length] = '\0';
    printf("FORTRAN opening file : %s\n", buffer);
    hipoReader.open(filename);
    *nrecords = hipoReader.getRecordCount();
    free(buffer);
  }

}
