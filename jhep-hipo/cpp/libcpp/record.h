/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * File:   hiporecord.h
 * Author: gavalian
 *
 * Created on April 11, 2017, 4:47 PM
 */

#ifndef HIPORECORD_H
#define HIPORECORD_H

#include <iostream>
#include <vector>
#include <string>
#include <stdio.h>
#include <stdlib.h>

#include "event.h"

namespace hipo {

    typedef struct {
        long  recordPosition;
        int   recordLength;
        int   recordEvents;
        int   recordDataOffset;
        int   recordDataLength;
        int   recordDataLengthUncompressed;
        int   startEvent;
        int   endEvent;
    } hipoRecordIndex_t;

    typedef struct {
        int signatureString; // 1) identifier string is HREC (int = 0x43455248
        int recordLength; // 2) TOTAL Length of the RECORD, includes INDEX array
        int recordDataLength; // 3) Length of the DATA uncompressed
        int recordDataLengthCompressed; // 4) compressed length of the DATA buffer
        int numberOfEvents ; // 5) number of event, data buckets in DATA buffer
        int headerLength ; // 6) Length of the buffer represengin HEADER for the record
        int indexDataLength ; // 7) Length of the index buffer (in bytes)
        int compressionType;
    } hipoRecordHeader_t;

    class record {

    private:

        std::vector< std::vector<char> > eventBuffer;
        char *getUncompressed(const char *data, int dataLength, int dataLengthUncompressed);

    public:

        record();
        ~record();

        void  init(const char *data, int dataLength, int dataLengthUncompressed, const char *index, int indexLength);
        int   getEventCount();
        void  addEvent(std::vector<char> &event);
        void  addEvent(hipo::event &event);
        int   getDataSize();
        std::vector<char> build();
        void  reset();
        std::vector<char>   getEvent(int index);
        hipo::event         getHipoEvent(int index);
        void                readHipoEvent(hipo::event &event, int index);

    };
}
#endif /* HIPORECORD_H */
