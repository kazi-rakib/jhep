#include <iostream>
#include "reader.h"
#include "event.h"

hipo::reader hipo_FORT_Reader;
hipo::record hipo_FORT_Record;
hipo::event  hipo_FORT_Event;

extern "C" {

  void hipo_open_file_(int *nrecords, const char *filename, int length){
    char *buffer = (char *) malloc(length+1);
    memcpy(buffer,filename,length);
    buffer[length] = '\0';
    printf("FORTRAN opening file : %s\n", buffer);
    hipo_FORT_Reader.open(buffer);
    hipo_FORT_Reader.showInfo();
    *nrecords = hipo_FORT_Reader.getRecordCount();
    free(buffer);
  }

  void hipo_read_record_(int *record, int *n_events){
     int pos = (*record) - 1;
     hipo_FORT_Reader.readRecord(hipo_FORT_Record, pos);
     int nevt = hipo_FORT_Record.getEventCount();
     *n_events = nevt;
  }

  void hipo_read_event_(int *n_event){
      int event_index = (*n_event) - 1;
      //std::vector<char> record_event;
      //hipo_FORT_Record.readEvent(record_event,event_index);
      hipo_FORT_Record.readHipoEvent(hipo_FORT_Event, event_index);
  }

  void hipo_read_node_float_(int *group, int *item, int *nread, float *buffer){
    int id_g = *group;
    int id_i = *item;
    std::vector<float> vec = hipo_FORT_Event.getFloat(id_g,id_i);
    for(int i = 0; i < vec.size(); i++){
       buffer[i] = vec[i];
    }
    *nread = vec.size();
  }

  void hipo_read_node_int_(int *group, int *item, int *nread, int *buffer){
    int id_g = *group;
    int id_i = *item;
    std::vector<int> vec = hipo_FORT_Event.getInt(id_g,id_i);
    for(int i = 0; i < vec.size(); i++){
       buffer[i] = vec[i];
    }
    *nread = vec.size();
  }

}
