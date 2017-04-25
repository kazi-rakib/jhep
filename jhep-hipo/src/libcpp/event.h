/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   event.h
 * Author: gavalian
 *
 * Created on April 12, 2017, 10:14 AM
 */

#ifndef EVENT_H
#define EVENT_H
#include <iostream>
#include <vector>
#include <cstring>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

namespace hipo {
    class event {
    private:
        std::vector<char> dataBuffer;
        
        
            
    public:
        
        event();
        ~event();
        
        void showInfo();
        void init(std::vector<char> &buffer);
        int  getEventNode(int group, int item);
        
        void appendNode(int group, int item, std::vector<int> &vec);
        void appendNode(int group, int item, std::vector<int16_t> &vec);
        void appendNode(int group, int item, std::vector<int8_t> &vec);
        void appendNode(int group, int item, std::vector<float> &vec);
        void appendNode(int group, int item, std::string &vec);
        
        std::vector<int>    getInt(   int group, int item);
        std::vector<float>  getFloat( int group, int item);
        
        std::vector<char> getEventBuffer();
        void reset();        
    };
}

#endif /* EVENT_H */

