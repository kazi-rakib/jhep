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

    
    int   record::getEventCount(){ return (unsigned int) eventBuffer.size();}
    
    std::vector<char>   record::getEvent(int index){
        return eventBuffer[index];
    }
    
    hipo::event    record::getHipoEvent(int index){
        hipo::event event;
        event.init(eventBuffer[index]);
        return event;
    }
}
