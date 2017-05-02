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

#include "record.h"

namespace hipo {

    class reader {
    
    private:
        
        std::vector<char>               header;
        std::ifstream                   inputStream;
        std::vector<hipoRecordIndex_t>  recordIndex;
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
        void  readRecord(record &record, int pos);
        void  showInfo();
        
    };
}
#endif /* HIPOFILE_H */

