/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author gavalian
 */
public class HipoFileHeader {
    
    public static final int FILE_HEADER_LENGTH = 72;
    public static final int FILE_IDENTIFIER    = 0x4F504948;
    public static final int FILE_VERSION       = 0x312E3056;
    
    public static final int OFFSET_FILE_SIZE = 8;
    public static final int OFFSET_FILE_HEADER_SIZE = 12;
    public static final byte[] HIPO_FILE_SIGNATURE_BYTES = new byte[]{'H','I','P','O','V','0','.','2'};

    
    private  ByteBuffer  fileHeader    = null;
    private  Boolean     isHeaderValid = false;
    
    public HipoFileHeader(){
        byte[] header = new byte[HipoFileHeader.FILE_HEADER_LENGTH];
        fileHeader = ByteBuffer.wrap(header);
        fileHeader.order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0 ; i < 8; i++) fileHeader.put(i, HipoFileHeader.HIPO_FILE_SIGNATURE_BYTES[i]);
        this.setHeaderSize(0);
        this.isHeaderValid = true;
    }
    
    public HipoFileHeader(byte[] header){
        fileHeader = ByteBuffer.wrap(header);
        fileHeader.order(ByteOrder.LITTLE_ENDIAN);
        this.isHeaderValid = true;
        if(fileHeader.getInt(0)!=HipoFileHeader.FILE_IDENTIFIER){
            System.out.println("[Hipo-File] ---> error : this is not a HIPO File.");
            this.isHeaderValid = false;
        }
    }
    
    public int getRecordStart(){
        return HipoFileHeader.FILE_HEADER_LENGTH + getHeaderSize();
    }
    
    public boolean isValid(){
        return this.isHeaderValid;
    }
    
    public int getHeaderSize(){
        int headerSize = fileHeader.getInt(HipoFileHeader.OFFSET_FILE_HEADER_SIZE);
        return headerSize;
    }
    
    public int getIdentifier(){
        return fileHeader.getInt(0);
    }
    
    public int getVersion(){
        return fileHeader.getInt(4);
    }
    
    public int getHeaderStart(){
        return HipoFileHeader.FILE_HEADER_LENGTH;
    }
    
    public final void setHeaderSize(int size){
        fileHeader.putInt(HipoFileHeader.OFFSET_FILE_HEADER_SIZE, size);
    }
    
    public ByteBuffer build(){
        return this.fileHeader;
    }
        
}
