/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.jnp.hipo.packing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.jnp.readers.TextFileReader;

/**
 *
 * @author gavalian
 */
public class DataPacking {
    
    public static final int     LOSSY = 1;
    public static final int  LOSELESS = 0;
    
    
    public static final int  FRAME_NO_DROP = 0;
    public static final int     FRAME_DROP = 1;
    
    private  int  totalSize = 0;
    private  int totalCount = 0;
    
    private  int    packingMode = LOSELESS;
    private  int  framedropMode = FRAME_NO_DROP;
    
    private  ByteBuffer   packedBuffer = null;
    private  int         reservedSpace = 8;
    
    RandomAccessFile outStream;
    RandomAccessFile outStreamRaw;
    
    
    public DataPacking(){
       /* byte[] buffer = new byte[256];
        packedBuffer = ByteBuffer.wrap(buffer);
        packedBuffer.order(ByteOrder.LITTLE_ENDIAN);
        try {
            outStream = new RandomAccessFile("data_file_pulses.bin", "rw");
            outStreamRaw = new RandomAccessFile("data_file_pulses_raw.bin", "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataPacking.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    public final void setReservedBytes(int res){
        this.reservedSpace = res;
    }
    
    public DataPacking setPackingMode(int mode){
        if(mode!=LOSSY&&mode!=LOSELESS){
            System.out.println("*** error *** unknown packing mode " + mode);
            return this;
        }
        packingMode = mode;
        return this;
    }
    
    public DataPacking setFrameDropMode(int mode){
        if(mode!=FRAME_NO_DROP&&mode!=FRAME_DROP){
            System.out.println("*** error *** unknown frame drop mode " + mode);
            return this;
        }
        framedropMode = mode;
        return this;
    }
    /**
     * Prints the content of byte buffer on the screen
     * @param buffer the byte array buffer
     * @param offset offset in the buffer to start from
     * @param length number of elements to print
     */
    public void showByteBuffer(ByteBuffer buffer, int offset, int length){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < length; i++){
            str.append(String.format("0x%02X ", buffer.get(i+offset)));
        }
        System.out.println(str.toString());
    }
    
    public ByteBuffer getByteBuffer(){
        return this.packedBuffer;
    }
    
    public void unpack(ByteBuffer buffer){
        System.out.println("******** UNPACK ");
        CompactPulseSegment segment = new CompactPulseSegment();
        CompactPulseSegment segmentT = new CompactPulseSegment();
        short[] data = new short[16];
        buffer.position(0);
        buffer.limit(buffer.capacity());
        int position = 0;
        for(int i = 0; i < 6; i++){
            System.out.println(" UNPACKING " + i);
            int npos = segment.unpack(buffer, data, position);
            System.out.println(" position = " + position + "  new position = " + npos);            
            position = npos;
            segmentT.processSegment(data, 0);
            System.out.println(segmentT);
        }
        System.out.println("******** DONE ");
    }
    
    public void packDebug(short[] pulse, int count){
        byte[] data = new byte[128];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        CompactPulseSegment segment = new CompactPulseSegment();
        segment.processSegment(pulse, count);
        
        if(segment.getHighByteCount()<5) return;
        System.out.println(segment);
        //int length = segment.packToBufferLossy(buffer, 0);
        int length = segment.packToBufferLossy(buffer, 0);
        System.out.print(String.format(" L = %4d : ", length));
        showByteBuffer(buffer,0,20);
        short[] dp = new short[100];
        segment.unpack(buffer, dp, 0);
        System.out.print(" PKG = ");
        for(int i = 0; i < 16; i++){
            System.out.print(String.format("%4d ",pulse[count*16+i]));
        }
        System.out.print("\n UNP = ");
        for(int i = 0; i < 16; i++){
            System.out.print(String.format("%4d ",dp[i]));
        }
        System.out.println();
    }    
    
    /**
     * pack the pulse into the buffer starting from position given
     * by offset.
     * @param buffer byte buffer to pack the array in
     * @param pulse pulse to pack
     * @param offset offset in the byte buffer 
     */
    public void pack(ByteBuffer buffer, short[] pulse, int offset){
        CompactPulseSegment segment = new CompactPulseSegment();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(buffer.capacity());
        int n_segments = pulse.length/16;
        int position   = offset;
        int size       = 0;
        for(int i = 0; i < n_segments; i++){
            segment.processSegment(pulse, i);
            int length = segment.packToBuffer(buffer, position);
            position  += length;
            size      += length;
        }
        buffer.limit(position);
    }
    
    
    public void pack(short[] pulse){
        
        CompactPulseSegment segment = new CompactPulseSegment();
        segment.setPackingMode(LOSSY);
        int size = 0;
        int position = 0;
        packedBuffer.limit(256);
        for(int i = 0; i < 6; i++){
            segment.processSegment(pulse, i);
            System.out.println(segment);
            size += segment.getPackingSize();
            //int length = segment.packToBuffer(packedBuffer, position);
            int length = segment.packToBufferLossy(packedBuffer, position);
            System.out.println(" SEGMENT " + i + " LENGTH = " + length);
            position+=length;
            //System.out.println();
        }
        packedBuffer.limit(position);
        System.out.println(" PACKED SIZE IN BYTES = " + size + " limit = " + packedBuffer.limit());
        totalSize += packedBuffer.limit();
        totalCount++;
        try {
            this.outStream.write(packedBuffer.array(), 0, packedBuffer.limit());
            byte[] array = new byte[pulse.length*2];
            ByteBuffer buffer = ByteBuffer.wrap(array);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            for(int i = 0; i < pulse.length; i++){
                buffer.putShort(i*2, pulse[i]);
            }
            outStreamRaw.write(buffer.array());
        } catch (IOException ex) {
            Logger.getLogger(DataPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void printSummary(){
        double average = ((double) totalSize) / totalCount;
        double ratio   = 208.0/average;
        System.out.println(String.format(" AVERAGE BYTES = %.2f  RATIO = %.2f", average,ratio));
    }
    
    public void close(){
        try {
            this.outStream.close();
            this.outStreamRaw.close();
        } catch (IOException ex) {
            Logger.getLogger(DataPacking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Class to handle pulse segment, it deals with 16 short segments
     */
    public static class CompactPulseSegment
    {
        
        private byte[]      dataLow = new byte[8];
        private byte[]     dataHigh = new byte[16];
        private short   dataMinimum = 0;
        private int     zeroLeading = 0;
        private int    zeroTrailing = 0;
        private int    packingMode  = DataPacking.LOSELESS;
        
        public CompactPulseSegment(){
            
        }
        
        
        public void setPackingMode(int mode){
            if(mode!=DataPacking.LOSELESS&&mode!=DataPacking.LOSSY){
                System.out.println("*** ERROR *** : unknown packing mode " + mode);
                return;
            }
            packingMode = mode;
        }
        /**
         * write lower 4 bits into the buffer with 8 bytes, 16 samples
         * @param buffer byte buffer to write to
         * @param position offset in the buffer
         */
        private void writeLow8(ByteBuffer buffer, int position){
            for(int i = 0; i < 8; i++){
                buffer.put(i+position, dataLow[i]);
            }
        }
        /**
         * write lower 4 bits into the buffer with 4 bytes, 16 samples
         * @param buffer byte buffer to write to
         * @param position offset in the buffer
         */
        private void writeLow4(ByteBuffer buffer, int position){
            for(int i = 0; i < 4; i++){
                byte value = this.getCombined(dataLow[i*2], dataLow[i*2+1]);
                buffer.put(i+position, value);
            }
        }
        /**
         * write lower 4 bits into the buffer with 2 bytes, 16 samples
         * @param buffer byte buffer to write to
         * @param position offset in the buffer
         */
        private void writeLow2(ByteBuffer buffer, int position){
            for(int i = 0; i < 2; i++){
                byte value = this.getCombined(
                        dataLow[i*4], dataLow[i*4+1], dataLow[i*4+2],dataLow[i*4+3]);
                buffer.put(i+position, value);
            }
        }
        /**
         * Processes the pulse and fills the internal arrays with
         * the sement minimum, segment lower 4 bit array and high bit array.
         * calculates the number of high bits that can be skipped.
         * @param pulse initial pulse array 
         * @param segment number of segment the array (offset is 16*segment)
         */
        public void processSegment(short[] pulse, int segment){

            int offset = segment*16;
            dataMinimum = pulse[offset];
            for(int i = 0; i < 16; i++){
                if(pulse[offset+i]<dataMinimum) dataMinimum = pulse[offset+i];
            }
            
            for (int i = 0; i < 8; i++){
                short value_1 = (short) (pulse[offset+i*2]   - dataMinimum);
                short value_2 = (short) (pulse[offset+i*2+1] - dataMinimum);
                byte byte_1 = (byte) ((0x0F&value_1));
                byte byte_2 = (byte) ((0x0F&value_2));

                dataLow[i] = 0;
                dataLow[i] = (byte) ((byte_1)|(byte_2<<4));
                /*System.out.println(" BIN " + i + " VALUE 1 = " + value_1 + " byte = " + byte_1 +
                        String.format(" LOW = 0x%02X", dataLow[i]));
                System.out.println(" BIN " + i + " VALUE 2 = " + value_2 + " byte = " + byte_2 + 
                         String.format(" LOW = 0x%02X", dataLow[i]));
                */
                dataHigh[i*2]   = (byte) ((value_1>>4)&0x00FF);
                dataHigh[i*2+1] = (byte) ((value_2>>4)&0x00FF);
            }
            
            /*for(int i = 0; i < 8; i++){
                System.out.println(" PACKING # " + i + " = " + dataLow[i]);
            }*/
            zeroLeading = 0;
            int j = 0;
            while(dataHigh[j]==0&&j<dataHigh.length-1) j++;
            zeroLeading = j;
            zeroTrailing = 0;
            j = dataHigh.length-1;
            while(dataHigh[j]==0&&j>=1) j--;
            zeroTrailing = dataHigh.length - j - 1;
        }
        
        public int packToBufferLossy(ByteBuffer buffer, int offset){
            
            int   n_high = this.getHighByteCount();
            short header = (short) (dataMinimum&0x0FFF);
            int   mode   = 0;
            
            if(n_high>0){
                header = (short) (header|(0x0001<<12));
                //header = (short) (header|(0x0001<<13));
                mode   = 0;
            } else {
                header = (short) (header|(0x0002<<13));
                mode   = 2;
            }
            
            int position = offset;
            buffer.putShort(position, header);
            position+=2;
            if(mode==0){
                int nskip = this.getHighByteSkip();
                this.writeLow8(buffer, position);
                position+=8;
                short header_high =  0;
                header_high = (short) ((nskip<<4)|header_high);
                header_high = (short) ((n_high)|header_high);
                System.out.println("packing : skip = " + nskip + " count = " + n_high);
                buffer.put(position, (byte) header_high);
                position++;
                for(int i = 0; i < n_high; i++){
                    buffer.put(position, dataHigh[nskip+i]);
                    position++;
                }
            }
            
            if(mode==1){
                int nskip = this.getHighByteSkip();
                /*for(int i = 0; i < 8; i+=2){
                    byte vec = this.getCombined(dataLow[i], dataLow[i+1]);
                    //dataLow[i+2],dataLow[i+3]);
                    buffer.put(position, vec);
                    position++;
                }*/
                this.writeLow4(buffer, position);
                position+=4;
                short header_high =  0;
                header_high = (short) ((nskip<<4)|header_high);
                header_high = (short) ((n_high)|header_high);
                System.out.println("packing : skip = " + nskip + " count = " + n_high);
                buffer.put(position, (byte) header_high);
                position++;
                for(int i = 0; i < n_high; i++){
                    buffer.put(position, dataHigh[nskip+i]);
                    position++;
                }
            } else {
                
               /* for(int i = 0; i < 8; i+=4){
                    byte vec = this.getCombined(dataLow[i], dataLow[i+1],
                            dataLow[i+2],dataLow[i+3]);
                    buffer.put(position, vec);
                    position++;
                }*/
               this.writeLow2(buffer, position);
               position+=2;
            }
            return position - offset;
        }
        /**
         * Unpack the pulse.
         * @param buffer
         * @param pulseSegment
         * @param offset
         * @return 
         */
        public int unpack(ByteBuffer buffer, short[] pulseSegment, int offset){
            int position = offset;
            short header = buffer.getShort(position);
            position+=2;
            int   mode   = (header>>13)&0x00000003;
            System.out.println(String.format(" MODE = %02X", mode));
            if(mode==0){
                short minimum = (short) (header&0x0FFF);
                for(int i = 0; i < 8; i++){
                    byte vec = buffer.get(position);
                    position++;
                    int v2 = (vec&0x0F);
                    int v1 = (vec&0xF0)>>4;
                    pulseSegment[i*2    ] = (short) (minimum + v2); 
                    pulseSegment[i*2 + 1] = (short) (minimum + v1);
                }
            }
            
            if(mode==1){
                short minimum = (short) (header&0x0FFF);
                //System.out.println(" MINIMUM MODE 1 = " + minimum);
                for(int i = 0; i < 4; i++){
                    byte vec = buffer.get(position);
                    position++;
                    int v1 = (vec&0x0F);
                    int v2 = (vec&0xF0)>>4;
                    pulseSegment[i*4    ] = (short) (minimum + v1); 
                    pulseSegment[i*4 + 1] = (short) (minimum + v1);
                    pulseSegment[i*4 + 2] = (short) (minimum + v2);
                    pulseSegment[i*4 + 3] = (short) (minimum + v2);
                }
            } 
            
            if(mode==2){
                short minimum = (short) (header&0x0FFF);
                //System.out.println(" MINIMUM MODE 2 = " + minimum);
                for(int i = 0; i < 2; i++){
                    byte vec = buffer.get(position);
                    position++;
                    int v1 = (vec&0xF0)>>4;
                    int v2 = (vec&0x0F);
                    pulseSegment[i*8    ] = (short) (minimum + v1); 
                    pulseSegment[i*8 + 1] = (short) (minimum + v1);
                    pulseSegment[i*8 + 2] = (short) (minimum + v1);
                    pulseSegment[i*8 + 3] = (short) (minimum + v1);
                    pulseSegment[i*8 + 4] = (short) (minimum + v2); 
                    pulseSegment[i*8 + 5] = (short) (minimum + v2);
                    pulseSegment[i*8 + 6] = (short) (minimum + v2);
                    pulseSegment[i*8 + 7] = (short) (minimum + v2);
                }
            }
            
            int highFlag = (header>>12)&0x00000001;
            if(highFlag!=0){
                //System.out.println(" Yes follows ");
                byte  trailer = buffer.get(position);
                //System.out.println(" position = " + position + " trailer = " + String.format("0x%02X", trailer));
                position++;

                int  skip = ((trailer>>4)&0x0F);
                int nhigh = (trailer&0x0F);
                for(int i = 0; i < 16; i++){
                    //System.out.println(" value = " + i + "  = " + pulseSegment[i]);
                }
                //System.out.println( " skip = " + skip + " n-high = " + nhigh );
                for(int i = 0 ; i < nhigh; i++){
                    byte   high = buffer.get(position);
                    position++;
                    short value = pulseSegment[i+skip];
                    //System.out.println(" DEBUG : " + i + "  value = " + value + "  high = " + high);
                    //value = (short) (( high<<4 )|value);
                    value = (short) (value + high*16);
                    //System.out.println(" value post = " + value);
                    pulseSegment[i+skip] = value;
                }
            }
            return position;
        }
        /**
         * Packs the segment data to the given buffer starting from
         * position offset.
         * @param buffer buffer to write the pulse to
         * @param offset offset in the buffer
         * @return 
         */
        public int packToBuffer(ByteBuffer buffer, int offset){
            
            //short header = (short) (dataMinimum&0x0FFF);
            int   n_high = this.getHighByteCount();
            short header = (short) (dataMinimum&0x0FFF);
            int   mode   = 0;

            
            if(n_high>0){
                header = (short) (header|(0x0001<<12));
            } else {
                
            }
            
            int position = offset;
            
            buffer.putShort(position, header);
            position+=2;

            for(int i =0; i < dataLow.length; i++){
                buffer.put(position, dataLow[i]);
                position++;
            }
            //buffer.put(dataLow, 0, dataLow.length);
            //position += dataLow.length;
            int   skip = this.getHighByteSkip();
            int length = this.getHighByteCount();
            short header_high =  0;
            header_high = (short) ((skip<<4)|header_high);
            header_high = (short) ((n_high)|header_high);
            buffer.put(position, (byte) header_high);
            position++;
            //System.out.println(" LENGTH = " + length + "  POSITION = " + position);
            for(int i = 0; i < length; i++){
                //System.out.println(" putting to position " + position + " i " + i + "  data " + dataHigh[i]
                //+ "  limit = " + buffer.limit() + " capacity = " + buffer.capacity());
                buffer.put(position, (byte) dataHigh[skip+i]);//dataHigh[i]);
                position++;
            }
            return (position - offset);
        }
        
        public int getHighByteCount(){
            int positionT = this.dataHigh.length - zeroTrailing;
           return positionT - zeroLeading; 
        }
        
        public int getHighByteSkip(){
            return zeroLeading;
        }
        /**
         * Combines two bytes containing 4 values of the pulse low bits
         * into 1 byte containing the averaged values of the neighboring
         * pulse samples.
         * @param v1
         * @param v2
         * @return 
         */
        private byte getCombined(byte v1, byte v2){
            int i1 = (v1&0x0F);
            int i2 = (v1&0xF0)>>4;
            int i3 = (i1+i2)/2;
            byte data = (byte) 0;
            data = (byte) (data | (i3));
            int k1 = (v2&0x0F);
            int k2 = (v2&0xF0)>>4;
            int k3 = (k1+k2)/2;
            data = (byte) (data | (k3<<4));
            //System.out.println(String.format("%d %d %d %d %X", i1,i2,k1,k2,data));
            return data;
        }        
        /**
         * combines 4 bytes (8 values) of lower 4 bit array into 1 byte (2 values)
         * of averaged values.
         * @param v1 byte number 1
         * @param v2 byte number 2
         * @param v3 byte number 3
         * @param v4 byte number 4
         * @return combined data
         */
        private byte getCombined(byte v1, byte v2, byte v3, byte v4){
            byte data = (byte) 0;
            int i1 = (v1&0x0F);
            int i2 = (v1&0xF0)>>4;
            int i3 = (v2&0x0F);
            int i4 = (v2&0xF0)>>4;
            int ia = (i1+i2+i3+i4)/4;
            if((i1+i2+i3+i4)%4>=2) ia++;
            int k1 = (v3&0x0F);
            int k2 = (v3&0xF0)>>4;
            int k3 = (v4&0x0F);
            int k4 = (v4&0xF0)>>4;
            int ka = (k1+k2+k3+k4)/4;
            if((k1+k2+k3+k4)%4>=2) ka++;
            data = (byte) (data | ia<<4);
            data = (byte) (data | ka);
            return data;
        }
        
        public int getPackingSize(){
            int size = 8 + 2;            
            if(packingMode==DataPacking.LOSSY) size -= 4;
            size += 1;
            if(getHighByteCount()>0){
                size += getHighByteCount();
            }
            return size;
        }
        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append(String.format("(%4d) : ", dataMinimum));
            for(int i =0; i < dataLow.length; i++) str.append(String.format(" 0x%02X", dataLow[i]));
            str.append(String.format("  || (%3d, %3d, %3d) || ", getHighByteCount(),zeroLeading, zeroTrailing));
            for(int i = 0; i < dataHigh.length; i++) str.append(String.format("%4d", dataHigh[i]));
            return str.toString();
        }
    }
    
    public static void main(String[] args){
        
        DataPacking packing = new DataPacking();
        TextFileReader reader = new TextFileReader();
        reader.setSeparator(",");
        reader.open("/Users/gavalian/Work/Software/project-3a.0.0/Distribution/pulses.dat");
        //reader.open("/Users/gavalian/Work/Software/project-3a.0.0/Distribution/tac_monitor_30853_Samples.txt");
        int counter = 0;
        while(reader.readNext()==true){
            //if(counter>25) break;
            short[] array = reader.getAsShortArray();
            System.out.println(" PULSE # " + counter);
            //packing.packDebug(array, 3);
//for(int i =0; i < 6; i++){
            //packing.packRegion(array, i*16);
            packing.pack(array);
            //ByteBuffer buffer = packing.getByteBuffer();
            //packing.unpack(buffer);
            //}
            //System.out.println(array.length);
            counter++;
        }
        packing.printSummary();
    }
}
