/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "record.h"
#include "hipoexceptions.h"
#include "event.h"

#ifdef __LZ4__
#include <lz4.h>
#endif

namespace hipo {
    
    
    record::record(){ }

    record::~record(){ }

    void record::init(const char *data, int dataLength, 
            int dataLengthUncompressed, const char *index, int indexLength){
                        
        //printf("--> decompress : %d  %d\n",dataLength,dataLengthUncompressed);
        
        char *uncompressed; 
        if(dataLength==dataLengthUncompressed){
            uncompressed = const_cast<char *>(data);
        } else {
            uncompressed = getUncompressed(data,dataLength,dataLengthUncompressed); 
        }
        if(uncompressed==NULL){
            printf("something went wrong the de-compressing\n");
            eventBuffer.clear();
            return;
        }
        int nindex = indexLength/4;
        const int *index_ptr = reinterpret_cast<const int*>(index);
        int position = 0;
        eventBuffer.clear();
        
        for(int i = 0; i < nindex; i++){
            int nbytes = index_ptr[i];
            std::vector<char> event; event.resize(nbytes);
            std::memcpy(&event[0],&uncompressed[position],nbytes);
            eventBuffer.push_back(event);
            position += nbytes;
        }
        if(dataLength!=dataLengthUncompressed){
            free(uncompressed);
        }
        
    }

    
    char *record::getUncompressed(const char *data, int dataLength, int dataLengthUncompressed){
        
#ifdef __LZ4__
        
        char *output = (char *) malloc(dataLengthUncompressed);
        int result = LZ4_decompress_safe(data,output,dataLength,dataLengthUncompressed);  
        return output;  
        //printf(" FIRST (%d) = %x %x %x %x\n",result);//,destUnCompressed[0],destUnCompressed[1],
        //destUnCompressed[2],destUnCompressed[3]);  
        //LZ4_decompress_fast(buffer,destUnCompressed,decompressedLength);  
        //LZ4_uncompress(buffer,destUnCompressed,decompressedLength);
#endif
        
#ifndef __LZ4__
        printf("\n   >>>>> LZ4 compression is not supported.");
        printf("\n   >>>>> check if libz4 is installed on your system.");  
        printf("\n   >>>>> recompile the library with liblz4 installed.\n");  
        return NULL;
#endif  
        
    }

    int   record::getDataSize(){
        int size = 0;
        for(int i = 0; i < eventBuffer.size(); i++){
            size += eventBuffer[i].size();
        }
        return size;
    }
    
    int   record::getEventCount(){ return (unsigned int) eventBuffer.size();}
    void  record::reset(){ eventBuffer.clear();}
    
    std::vector<char>   record::getEvent(int index){
        return eventBuffer[index];
    }
    
    hipo::event    record::getHipoEvent(int index){
        hipo::event event;
        event.init(eventBuffer[index]);
        return event;
    }
    
    void record::addEvent(std::vector<char>& event){
        eventBuffer.push_back(event);
    }
    
    void record::addEvent(hipo::event& event){
        eventBuffer.push_back(event.getEventBuffer());
    }
    
    std::vector<char> record::build(){
        
        int headerSize = 40;
        int indexSize  = eventBuffer.size()*sizeof(int32_t);
        int eventBufferSize = 0;
        
        for(int i = 0; i < eventBuffer.size(); i++){
            eventBufferSize += eventBuffer[i].size();
        }
        
        std::vector<char> recordVec(headerSize + indexSize + eventBufferSize);
        
        recordVec[0] = 'H'; recordVec[1] = 'R'; recordVec[2] = 'E'; recordVec[3] = 'C';

        uint32_t  *recordLength_ptr = reinterpret_cast<uint32_t*>(&recordVec[4]);
        uint32_t   *dataLengthU_ptr = reinterpret_cast<uint32_t*>(&recordVec[8]);
        uint32_t   *dataLengthC_ptr = reinterpret_cast<uint32_t*>(&recordVec[12]);        
        uint32_t     *numEvents_ptr = reinterpret_cast<uint32_t*>(&recordVec[16]);
        uint32_t  *headerLength_ptr = reinterpret_cast<uint32_t*>(&recordVec[20]);
        uint32_t   *indexLength_ptr = reinterpret_cast<uint32_t*>(&recordVec[24]);
        
        *recordLength_ptr = recordVec.size();
        *dataLengthU_ptr  = eventBufferSize;
        *dataLengthC_ptr  = eventBufferSize;
        *numEvents_ptr    = eventBuffer.size();
        *headerLength_ptr = 0;
        *indexLength_ptr  = indexSize;
        
        
        int indexPosition = 40;
        for(int i = 0; i < eventBuffer.size(); i++){
            uint32_t *index_ptr = reinterpret_cast<uint32_t*>(&recordVec[indexPosition]);
            *index_ptr = eventBuffer[i].size();
            indexPosition += 4;
        }
        
        int dataPosition = indexPosition;
        for(int i = 0; i < eventBuffer.size(); i++){
            std:memcpy(&recordVec[dataPosition],& ((eventBuffer[i])[0]), eventBuffer[i].size());
            dataPosition += eventBuffer[i].size();
        }
        
        return recordVec;
    }
}
