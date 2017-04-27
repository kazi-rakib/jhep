/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   node.h
 * Author: gavalian
 *
 * Created on April 27, 2017, 10:01 AM
 */

#ifndef NODE_H
#define NODE_H

#include <iostream>
#include <cstdlib>
#include <cstdio>

#include "event.h"

namespace hipo {
    
template <class T>

class node {
    private:
        int length;
        T   *ptr;
    public:
        
        node();
        node(int group, int item,hipo::event &event);
        
        virtual ~node();
        
        T getValue(int index);
        //void setNode(int group, int item, hipo::event &event);
        void show();
        int  getLength();
        void setLength(int l);
        void setAddress(char *address);
};

}

namespace hipo {
    
    template <class T> node<T>::node(){
        
    }
    
    template <class T> node<T>::node(int group, int item,hipo::event &event){
      int address = event.getNodeAddress(group,item);
      if(address<0){
          length = 0;
          ptr    = NULL;
      } else {
          length = event.getNodeSize(address);
          ptr    = reinterpret_cast<T*>(event.getNodePtr(address));
      }
    }
    
    template <class T> node<T>::~node(){
        length = 0;
        ptr    = 0L;
    }
    
    template <class T> void node<T>::setLength(int l){
        length = l;
    }
    
    template <class T> void node<T>::setAddress(char* address){
        ptr = reinterpret_cast<T*>(address);
    }
    
    template <class T> int node<T>::getLength(){ return length;}
    
    template <class T> T node<T>::getValue(int index){
        return ptr[index];
    }
    
    template <class T> void node<T>::show(){
        std::cout << " NODE LENGTH = " << length << '\n';
    }
}
#endif /* NODE_H */

