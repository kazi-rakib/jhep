/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * File:   hipofile.h
 * Author: gavalian
 *
 * Created on April 11, 2017, 2:07 PM
 */

#ifndef HIPOFILE_H
#define HIPOFILE_H


#define HIPO_FILE_HEADER_SIZE 72

#include <iostream>
#include <vector>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>
#include <memory>

#include "record.h"
#include "node.h"
#include "event.h"

namespace hipo {

    class reader {

    private:

        std::vector<char>               header;
        std::ifstream                   inputStream;
        std::vector<hipoRecordIndex_t>  recordIndex;
        record                          reader_record;
        event                           reader_event;
        //std::vector <std::shared_ptr<leaf> > Leafs;
        std::vector< std::shared_ptr<hipo::generic_node> > nodeList;

    public:

        reader();
        ~reader();

        void  open(const char *filename);

        void  readHeader();
        void  readRecordIndex();

        int   getFirstRecordsPosition();
        int   getHeaderLength();
        int   getSignature();
        int   getRecordCount();
        int   getEventCount();

        void  readRecord(record &record, int pos);
        void  readRecord(int index);
        void  readEvent(int index);
        void  addNode(std::shared_ptr<hipo::generic_node> node_ptr);
        void  showInfo();
        void  printWarning();

        template<class T> hipo::node<T> *getNode(int group, int item);
    };

template<class T> hipo::node<T> *reader::getNode(int group, int item){
    hipo::node<T> *__leaf_node = new hipo::node<T>(group,item);
    addNode(std::shared_ptr<generic_node> (__leaf_node) );
    return __leaf_node;
}

}
#endif /* HIPOFILE_H */
