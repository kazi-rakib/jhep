/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   hipo_utils.h
 * Author: gavalian
 *
 * Created on March 14, 2017, 2:30 PM
 */

#ifndef HIPO_UTILS_H
#define HIPO_UTILS_H

#ifdef __cplusplus
extern "C" {
#endif

#include "hipo_file.h"
#include "hipo_record.h"

    extern hipo_file_t    hipo_file;
    extern record_index_t hipo_file_index;
    extern hipo_record_t  hipo_record;
    
    void  openFile(const char *filename);
    int   getEntries();
    int   readRecord(int record);
    int   getEvents();
    int   readEvent(int event);
    void  readNode(int group, int item);
#ifdef __cplusplus
}
#endif

#endif /* HIPO_UTILS_H */

