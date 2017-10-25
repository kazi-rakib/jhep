/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * File:   read_file.cpp
 * Author: gavalian
 *
 * Created on April 11, 2017, 4:30 PM
 */

#include <cstdlib>
#include <iostream>

#include "reader.h"
#include "writer.h"
#include "event.h"
#include "node.h"
#include "text.h"
#include "data.h"


using namespace std;
/*
 *
 */
int main(int argc, char** argv) {

    std::cout << "start file reading test" << '\n';

    char filename[128];

    if(argc>1){
        sprintf(filename,"%s",argv[1]);
    } else {
      printf(" \n\n Please provide a filename to read....\n\n");
      exit(0);
    }
    printf("-----> open file : %s\n",filename);
    hipo::reader reader;
    reader.open(filename);
    reader.showInfo();

    hipo::node<int>       *pid = reader.getNode<int>(22,1);
    hipo::node<float>      *px = reader.getNode<float>(22,2);
    hipo::node<float>      *py = reader.getNode<float>(22,3);
    hipo::node<float>      *pz = reader.getNode<float>(22,4);
    hipo::node<int8_t> *charge = reader.getNode<int8_t>(22,8);

    int nrecords = reader.getRecordCount();

    printf("-----> file contains %d records\n",nrecords);
    printf("\n\n");
    printf("-----> start reading records.\n");

    int ecounter = 0;
    for(int i = 0; i < nrecords; i++){
       reader.readRecord(i);
       int nevents = reader.getEventCount();
       for(int k = 0; k < nevents; k++){
         //printf("-----> reading event # %d\n",ecounter);
         reader.readEvent(k);
         int size = px->getLength();
         for(int s = 0; s < size; s++){
           printf(" %8d %8.3f %8.3f %8.3f %2d\n",pid->getValue(s),
                  px->getValue(s),py->getValue(s),pz->getValue(s),charge->getValue(s));
         }
         //printf("\n");
         ecounter++;
       }
    }
    printf("-----> done reading records. n events = %d\n",ecounter);
    return 0;
}
