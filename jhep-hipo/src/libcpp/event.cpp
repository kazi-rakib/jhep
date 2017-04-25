/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "event.h"

namespace hipo {
    
    event::event(){
        reset();
    }
    
    event::~event(){
        
    }
    
    void event::init(std::vector<char> &buffer){
        dataBuffer.resize(buffer.size());
        std::memcpy(&dataBuffer[0],&buffer[0],buffer.size());
    }
    
    void event::appendNode(int group, int item, std::string& vec){
        int     size = dataBuffer.size();
        int datasize = vec.length();
        dataBuffer.resize(size + datasize + 8 ,0);
        uint16_t *group_ptr = reinterpret_cast<uint16_t*>(&dataBuffer[size]);
        uint8_t   *item_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+2]);
        uint8_t   *type_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+3]);
        uint32_t *length_ptr = reinterpret_cast<uint32_t*>(&dataBuffer[size+4]);
        *group_ptr  = group;
        *item_ptr   = item;
        *type_ptr   = 6;
        *length_ptr = datasize;
        std::memcpy(&dataBuffer[size+8],(char *) &vec[0],datasize);
    }
    
    void event::appendNode(int group, int item, std::vector<int8_t> &vec){
        int     size = dataBuffer.size();
        int datasize = vec.size()*sizeof(int8_t);
        
        dataBuffer.resize(size + datasize + 8 ,0);
        uint16_t *group_ptr = reinterpret_cast<uint16_t*>(&dataBuffer[size]);
        uint8_t   *item_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+2]);
        uint8_t   *type_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+3]);
        uint32_t *length_ptr = reinterpret_cast<uint32_t*>(&dataBuffer[size+4]);
        *group_ptr  = group;
        *item_ptr   = item;
        *type_ptr   = 2;
        *length_ptr = datasize;
        std::memcpy(&dataBuffer[size+8],(char *) &vec[0],datasize);
    }
    
    void event::appendNode(int group, int item, std::vector<int16_t> &vec){
        int     size = dataBuffer.size();
        int datasize = vec.size()*sizeof(int16_t);
        
        dataBuffer.resize(size + datasize + 8 ,0);
        uint16_t *group_ptr = reinterpret_cast<uint16_t*>(&dataBuffer[size]);
        uint8_t   *item_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+2]);
        uint8_t   *type_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+3]);
        uint32_t *length_ptr = reinterpret_cast<uint32_t*>(&dataBuffer[size+4]);
        *group_ptr  = group;
        *item_ptr   = item;
        *type_ptr   = 2;
        *length_ptr = datasize;
        std::memcpy(&dataBuffer[size+8],(char *) &vec[0],datasize);
    }
    
    void event::appendNode(int group, int item, std::vector<int> &vec){
        int     size = dataBuffer.size();
        int datasize = vec.size()*sizeof(int);
        
        dataBuffer.resize(size + datasize + 8 ,0);
        uint16_t  *group_ptr = reinterpret_cast<uint16_t*>(&dataBuffer[size]);
        uint8_t    *item_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+2]);
        uint8_t    *type_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+3]);
        uint32_t *length_ptr = reinterpret_cast<uint32_t*>(&dataBuffer[size+4]);
        *group_ptr  = group;
        *item_ptr   = item;
        *type_ptr   = 3;
        *length_ptr = datasize;
        std::memcpy(&dataBuffer[size+8],(char *) &vec[0],datasize);
    }
    
    
    
    void event::appendNode(int group, int item, std::vector<float> &vec){
        
        int     size = dataBuffer.size();
        int datasize = vec.size()*sizeof(float);
        
        dataBuffer.resize(size + datasize + 8 ,0);
        uint16_t  *group_ptr = reinterpret_cast<uint16_t*>(&dataBuffer[size]);
        uint8_t    *item_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+2]);
        uint8_t    *type_ptr = reinterpret_cast<uint8_t*>(&dataBuffer[size+3]);
        uint32_t *length_ptr = reinterpret_cast<uint32_t*>(&dataBuffer[size+4]);
        *group_ptr  = group;
        *item_ptr   = item;
        *type_ptr   = 4;
        *length_ptr = datasize;
        std::memcpy(&dataBuffer[size+8],(char *) &vec[0],datasize);
    }
    
    void event::reset(){
        dataBuffer.resize(8);
        dataBuffer[0] = 'E'; dataBuffer[1] = 'V';
        dataBuffer[2] = 'N'; dataBuffer[3] = 'T';
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
    
    
    std::vector<char> event::getEventBuffer(){ return dataBuffer;}
    
    void event::showInfo(){
        printf(" EVENT SIGNATURE =  SIZE = %lu\n",dataBuffer.size());
    }
}
