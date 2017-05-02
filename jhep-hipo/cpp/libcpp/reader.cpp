/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "reader.h"
#include "hipoexceptions.h"
#include "record.h"
#include <cstdlib>

namespace hipo {
reader::reader(){

}

reader::~reader(){

}

void reader::open(const char *filename){

    inputStream.open(filename, std::ios::binary);
    if(inputStream.is_open()==false){
      printf("[ERROR] something went wrong with openning file : %s\n",
            filename);
      return;
    }
    readHeader();
    readRecordIndex();
}

void reader::readHeader(){
    header.resize(80);
    inputStream.read(&header[0],80);
    int *signature = reinterpret_cast<int *>(&header[0]);
    //printf("signature = %X\n",(unsigned int) *signature);
    //std::cout << "signature = " << std::ios::hex << (*signature) << '\n';
}

int   reader::getHeaderLength(){
    int *headerLength = reinterpret_cast<int *>(&header[12]);
    return *headerLength;
}

int   reader::getSignature(){
    int *signature = reinterpret_cast<int *>(&header[0]);
    return *signature;
}

void  reader::readRecordIndex(){

    inputStream.seekg(0,std::ios::end);
    long hipoFileSize = inputStream.tellg();
    long positionOffset = HIPO_FILE_HEADER_SIZE + getHeaderLength();
    recordIndex.clear();
    inputStream.seekg(positionOffset,std::ios::beg);
    hipoRecordHeader_t recordHeader;
    int icounter   = 0;
    int eventCount = 0;
    while(positionOffset+40<hipoFileSize){
        inputStream.read( (char *) &recordHeader,sizeof(hipoRecordHeader_t));
        int dataLength    = (recordHeader.recordDataLengthCompressed&0x00FFFFFF);
        /*printf("---------------------------------------\n");
        printf("     signature = %X\n",(unsigned int) recordHeader.signatureString);
        printf(" header length = %d\n",recordHeader.headerLength);
        printf("   data length = %d\n",recordHeader.recordDataLength);
        printf("   data length = %d\n",dataLength);
        printf("  index length = %d\n",recordHeader.indexDataLength);
         */
        hipoRecordIndex_t  rindex;
        rindex.recordPosition = positionOffset;
        rindex.recordLength   = 40 + dataLength + recordHeader.headerLength + recordHeader.indexDataLength;
        rindex.recordEvents   = recordHeader.numberOfEvents;
        rindex.recordDataOffset = 40 + recordHeader.headerLength + recordHeader.indexDataLength;
        rindex.recordDataLengthUncompressed = recordHeader.recordDataLength;

        recordIndex.push_back(rindex);

        positionOffset += 40 + dataLength + recordHeader.headerLength + recordHeader.indexDataLength;
        inputStream.seekg(positionOffset,std::ios::beg);

        //positionOffset +=
        icounter++;
    }
    printf("total records = %d  index array = %d\n",icounter, (unsigned int) recordIndex.size());
}

int   reader::getRecordCount(){
    return (unsigned int) recordIndex.size();
}

void  reader::readRecord(record &record,int pos){
    hipoRecordIndex_t rindex = recordIndex[pos];

    long   dataPosition  = rindex.recordPosition + rindex.recordDataOffset;
    long  indexPosition  = rindex.recordPosition + 40 ;
    int     dataLength  = rindex.recordLength - rindex.recordDataOffset;
    int     dataLengthU = (rindex.recordDataLengthUncompressed&0x00FFFFFF);
    int     indexLength = rindex.recordLength - dataLength - 40;
    //printf("-------------------\n");
    //printf(" reading record %d at position %ld size = %d\n",pos,rindex.recordPosition,rindex.recordLength);
    //printf(" record data length = %d position = %ld index length = %d\n", dataLength,dataPosition,indexLength);

    char *index = ( char *) malloc(dataLength);
    char  *data = ( char *) malloc(dataLength);

    inputStream.seekg(indexPosition,std::ios::beg);
    inputStream.read(index,indexLength);

    inputStream.seekg(dataPosition,std::ios::beg);
    inputStream.read(data,dataLength);

    int *signature = reinterpret_cast<int*>(data);
    //printf(" data signature = %X\n",(unsigned int) *signature);
    record.init(data,dataLength,dataLengthU,index,indexLength);

    free(index);
    free(data);
}


void reader::showInfo(){
    if(recordIndex.size()<1){
      printf(" there are no records in the file : %d\n", inputStream.is_open());
      return;
    }
    long recordPosition = recordIndex[recordIndex.size()-1].recordPosition;
    float sizePos = recordPosition/1024.0/1024.0;
    printf("-------------------------------------\n");
    printf("     signature : %X\n", (unsigned int) getSignature());
    printf(" header Length : %d bytes\n", (unsigned int) getHeaderLength());
    printf("   file Length : %.2f MB\n", sizePos);
    printf("-------------------------------------\n");
}

}
