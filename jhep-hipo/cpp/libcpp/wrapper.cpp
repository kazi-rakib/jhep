#include <iostream>
#include "reader.h"

hipo::reader hipo_FORT_Reader;
hipo::record hipo_FORT_Record;

extern "C" {

  void hipo_open_file_(int *nrecords, const char *filename, int length){
    char *buffer = (char *) malloc(length+1);
    memcpy(buffer,filename,length);
    buffer[length] = '\0';
    printf("FORTRAN opening file : %s\n", buffer);
    hipo_FORT_Reader.open(filename);
    *nrecords = hipo_FORT_Reader.getRecordCount();
    free(buffer);
  }

  void hipo_read_record_(int *record, int *n_events){
     int pos = (*record) - 1;
     hipo_FORT_Reader.readRecord(hipo_FORT_Record, pos);
     int nevt = hipo_FORT_Record.getEventCount();
     *n_events = nevt;
  }
  
}
