/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   data_utils.h
 * Author: gavalian
 *
 * Created on March 14, 2017, 10:14 AM
 */

#ifndef DATA_UTILS_H
#define DATA_UTILS_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

    typedef struct {
        int size;
        void *buffer;
    } data_buffer;
    
    void data_allocate(data_buffer *buffer, int size);
    void data_expand(data_buffer *buffer, int size);
    void data_free(data_buffer *buffer);
    
    uint8_t   data_read_byte  ( data_buffer *buffer, int position);
    uint16_t  data_read_short ( data_buffer *buffer, int position);
    int       data_read_int   ( data_buffer *buffer, int position);
    uint64_t  data_read_long  ( data_buffer *buffer, int position);
    float     data_read_float ( data_buffer *buffer, int position);
    
    void data_write_int  ( data_buffer *buffer, int position, int value);
    void data_write_long ( data_buffer *buffer, int position, uint64_t value);

    void data_show(data_buffer *buffer);
    void data_print(data_buffer *buffer, int wrap, int max);
    
#ifdef __cplusplus
}
#endif

#endif /* DATA_UTILS_H */

