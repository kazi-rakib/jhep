/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   hipodebug.cpp
 * Author: gavalian
 *
 * Created on April 11, 2017, 4:30 PM
 */

#include <cstdlib>
#include <iostream>

#include "reader.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
    
    std::cout << "start debug program" << '\n';
    
    char filename[128];
    
    if(argc>0){
        sprintf(filename,"%s",argv[1]);
    } else {
        sprintf(filename,"%s","/Users/gavalian/Work/Software/project-1a.0.0/clas12dst_000761.hipo");
    }
    printf("--> open file : %s\n",filename);
    
    hipo::reader reader;
    
    
    reader.open(filename);
    reader.showInfo();
    reader.readRecordIndex();
    
    hipo::record record;
    
    int nrecords = reader.getRecordCount();
    printf("# records = %d\n",nrecords);
    
    for(int i = 0; i < nrecords; i++){
        reader.readRecord(record,i);
        int nevents = record.getEventCount();
        //printf("-----------> n events = %d\n",nevents);
        std::vector<char> event = record.getEvent(0);
        
        int *ptr = reinterpret_cast<int *>( &event[0]);
        //printf(" signature = %X\n",(unsigned int) ptr[0]);
        
        for(int k = 0; k < nevents; k++){
            hipo::event hipoEvent = record.getHipoEvent(k);
            //hipoEvent.showInfo();
            //int node = hipoEvent.getEventNode(1200,0);
            std::vector<int>      pid = hipoEvent.getInt(22,1);
            std::vector<int>   charge = hipoEvent.getInt(22,8);
            std::vector<float>     px = hipoEvent.getFloat(22,2);
            
            printf(" pid size = %lu charge size = %lu  px size = %lu\n",
                    pid.size(),charge.size(), px.size());
        }
    }
    return 0;
}

