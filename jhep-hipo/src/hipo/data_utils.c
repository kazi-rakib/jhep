/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "data_utils.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void data_allocate(data_buffer *buffer, int size){
    //data_free(buffer);
    buffer->buffer = malloc(size);
    buffer->size   = size;
}

void data_expand(data_buffer *buffer, int size){
    void *data = malloc(buffer->size+size);
    int   prev_size = buffer->size;
    memcpy((char *) data, (char *) buffer->buffer, buffer->size);
    free(buffer->buffer);
    buffer->buffer = data;
    buffer->size   = prev_size + size;
}

void data_free(data_buffer *buffer){
    if(buffer->buffer!=NULL){
        free(buffer->buffer);buffer->size = 0; buffer->buffer = NULL;
    }
}
int  data_read_int(data_buffer *buffer, int position){
    if(position>=0&&position+4<buffer->size){
        char *array = (char *) buffer->buffer;
        int *pointer = (int *) &array[position];
        return *pointer;
    }
    return 0;
}

uint8_t   data_read_byte  ( data_buffer *buffer, int position){
    if(position>=0&&position<buffer->size){
        char *array = (char *) buffer->buffer;
        uint8_t *pointer = (uint8_t *) &array[position];
        return *pointer;
    }
    return (uint8_t) 0;
}

uint16_t  data_read_short ( data_buffer *buffer, int position){
    if(position>=0&&position+2<buffer->size){
        char *array = (char *) buffer->buffer;
        uint16_t *pointer = (uint16_t *) &array[position];
        return *pointer;
    }
    return 0;
}
uint64_t  data_read_long  ( data_buffer *buffer, int position)
{
    if(position>=0&&position+8<buffer->size){
        char       *array = (char *) buffer->buffer;
        uint64_t *pointer = (uint64_t *) &array[position];
        return   *pointer;
    }
    return 0;
}

float     data_read_float ( data_buffer *buffer, int position){
    if(position>=0&&position+4<buffer->size){
        char *array = (char *) buffer->buffer;
        float *pointer = (float *) &array[position];
        return *pointer;
    }
    return 0.0;
}



void data_write_int(data_buffer *buffer, int position, int value){
    if(position>=0&&position+4<buffer->size){
        char *array = (char *) buffer->buffer;
        int *pointer = (int *) &array[position];
        *pointer = value;
    }
}

void data_write_long ( data_buffer *buffer, int position, uint64_t value){
    if(position>=0&&position+8<buffer->size){
        char *array = (char *) buffer->buffer;
        uint64_t *pointer = (uint64_t *) &array[position];
        *pointer = value;
    }
}
void data_show(data_buffer *buffer){
    printf("data buffer size = %12d pointer = %0X\n",buffer->size,(unsigned int) ((char *) buffer->buffer));    
}

void data_print(data_buffer *buffer, int wrap, int max){
    for(int i = 0; i < buffer->size; i++){
        printf(" %3X ", (unsigned int) data_read_byte(buffer,i));
        if(max>0 && i>max){
            printf("\n"); return;
        }
        if( (i+1)%wrap==0){
            printf("\n");
        }
    }
    printf("\n");
}