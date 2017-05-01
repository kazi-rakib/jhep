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
#include "writer.h"
#include "event.h"
#include "node.h"

using namespace std;


void readFile(char* filename){
    hipo::reader reader;


    reader.open(filename);
    reader.showInfo();
    reader.readRecordIndex();

    hipo::record record;

    int nrecords = reader.getRecordCount();
    printf("# records = %d\n",nrecords);
    int type;
    double mom;
    for(int i = 0; i < nrecords; i++){
        reader.readRecord(record,i);
        int nevents = record.getEventCount();
        //printf("-----------> n events = %d\n",nevents);
        //std::vector<char> event = record.getEvent(0);

        //int *ptr = reinterpret_cast<int *>( &event[0]);
        //printf(" signature = %X\n",(unsigned int) ptr[0]);


        for(int k = 0; k < nevents; k++){

            hipo::event hipoEvent = record.getHipoEvent(k);
            //hipoEvent.scanEvent();
            //hipoEvent.showInfo();
            //int node = hipoEvent.getEventNode(1200,0);

            std::vector<int>      pid = hipoEvent.getInt(22,1);
            //std::vector<int>   charge = hipoEvent.getInt(22,8);
            std::vector<float>     px = hipoEvent.getFloat(22,2);
            std::vector<float>     py = hipoEvent.getFloat(22,3);
            std::vector<float>     pz = hipoEvent.getFloat(22,4);

            for(int v = 0; v < px.size(); v++){
                mom += px[v] + py[v] + pz[v];
            }

            hipo::node<int>    pidn(22,1,hipoEvent);
            hipo::node<float>  pxn(22,2,hipoEvent);
            hipo::node<float>  pyn(22,3,hipoEvent);
            hipo::node<float>  pzn(22,4,hipoEvent);

            for(int vn = 0; vn < pxn.getLength(); vn++){
                printf(" %f %f %f %f %f %f\n",pxn.getValue(vn),pyn.getValue(vn),pzn.getValue(vn),
                        px[vn],py[vn],pz[vn]);
            }
            printf("node size = %d  %d\n",pidn.getLength(),pid.size());
           // printf(" pid size = %ld charge size = %lu  px size = %lu\n",
           //         pid.size(),charge.size(), px.size());

        }

    }
    printf(" total momentum = %f\n",mom);
}

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

    readFile(filename);


    hipo::record record;

    /*
    hipo::event event;

    event.reset();
    event.showInfo();
    std::vector<int> vecInt;
    std::vector<float> vecFloat;

    vecInt.resize(12,45);
    vecFloat.resize(12,1.2345);

    event.appendNode(1200,1,vecInt);
    event.appendNode(1200,2,vecFloat);
    event.showInfo();


    hipo::node<int> intNode (1200,1,event);
    intNode.show();

    std::vector<int> node = event.getInt(1200,1);
    for(int i = 0; i < node.size(); i++){
        std::cout << i << "  " << node[i] << '\n';
    }
    std::cout << "\n\n";
    std::vector<float> nodeF = event.getFloat(1200,2);
    for(int i = 0; i < nodeF.size(); i++){
        std::cout << i << "  " << nodeF[i] << '\n';
    }

    hipo::record  record;

    hipo::record  recordDict;
    hipo::event   eventDict;
    std::string  schema("{1200,mc::data}[1,pid,INT][2,mass,FLOAT]");
    eventDict.appendNode(32111,1,schema);

    recordDict.addEvent(eventDict);
    std::vector<char> bufferDict = recordDict.build();

    record.addEvent(event);

    std::vector<char> vec = record.build();
    std::cout << " record size = " << vec.size() << '\n';

    hipo::writer writer;
    writer.open("test.hipo",bufferDict);

    for(int i = 0; i < 15; i++){
        writer.writeEvent(event);
    }

    writer.close();
    */
    return 0;
}
