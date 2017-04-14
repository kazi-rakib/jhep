/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "event.h"

namespace hipo {
    
    event::event(){
        
    }
    
    event::~event(){
        
    }
    
    void event::init(std::vector<char> &buffer){
        dataBuffer.resize(buffer.size());
        std::memcpy(&dataBuffer[0],&buffer[0],buffer.size());
    }
    
    int event::getEventNode(int group, int item){
        int position = 8;
        while(position+8<dataBuffer.size()){
            uint16_t   gid = *(reinterpret_cast<uint16_t*>(&dataBuffer[position]));
            uint8_t    iid = *(reinterpret_cast<uint8_t*>(&dataBuffer[position+2])); 
            uint8_t   type = *(reinterpret_cast<uint8_t*>(&dataBuffer[position+3]));
            int     length = *(reinterpret_cast<int*>(&dataBuffer[position+4])); 
            //printf("group = %4d , item = %4d\n",(unsigned int) gid, (unsigned int) iid);
            if(gid==group&&iid==item) return position;
            position += (length + 8);
        }
        
        return -1;
    }
    
    std::vector<int>    event::getInt(   int group, int item){
        int position = getEventNode(group,item);
        std::vector<int> vector;
        if(position>=0){
            uint16_t   gid = *(reinterpret_cast<uint16_t*>(&dataBuffer[position]));
            uint8_t    iid = *(reinterpret_cast<uint8_t*>(&dataBuffer[position+2])); 
            uint8_t   type = *(reinterpret_cast<uint8_t*>(&dataBuffer[position+3]));
            int     length = *(reinterpret_cast<int*>(&dataBuffer[position+4]));
            
            if(type==1){
                int    iter = length;
                for(int i = 0; i < iter; i++){
                    int8_t *ptr = reinterpret_cast<int8_t *>(&dataBuffer[position + 8 + i]);
                    vector.push_back( (int) *ptr);                    
                }
            }
            
            if(type==2){
                int    iter = length/2;
                for(int i = 0; i < iter; i++){
                    int16_t *ptr = reinterpret_cast<int16_t *>(&dataBuffer[position + 8 + i*2]);
                    vector.push_back( (int) *ptr);                    
                }
            }
    
            if(type==3){
                int    iter = length/4;
                for(int i = 0; i < iter; i++){
                    int *ptr = reinterpret_cast<int *>(&dataBuffer[position + 8 + i*4]);
                    vector.push_back( (int) *ptr);                    
                }
            }
            
        }
        return vector;;
    }
    
    std::vector<float>  event::getFloat( int group, int item){
        int position = getEventNode(group,item);
        std::vector<float> vector;
        if(position>=0){
            uint16_t   gid = *(reinterpret_cast<uint16_t*>(&dataBuffer[position]));
            uint8_t    iid = *(reinterpret_cast<uint8_t*>(&dataBuffer[position+2])); 
            uint8_t   type = *(reinterpret_cast<uint8_t*>(&dataBuffer[position+3]));
            int     length = *(reinterpret_cast<int*>(&dataBuffer[position+4]));
            if(type==4){
                int    iter = length/4;
                for(int i = 0; i < iter; i++){
                    float *ptr = reinterpret_cast<float *>(&dataBuffer[position + 8 + i*4]);
                    vector.push_back( *ptr);                    
                }
            }
        }
        return vector;
    }
    
    void event::showInfo(){
        printf(" EVENT SIGNATURE =  SIZE = %lu\n",dataBuffer.size());
    }
}
