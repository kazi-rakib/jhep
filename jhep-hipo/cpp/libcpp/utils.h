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

#ifndef UTILS_H
#define UTILS_H

#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <string>
#include <vector>

namespace hipo {
    
  class utils {
  private:
    
  public:
    utils();
    ~utils();
    static void tokenize(const std::string& str,
			 std::vector<std::string>& tokens,
			 const std::string& delimiters = " ");
    
  };

}

#endif /* UTILS_H */

