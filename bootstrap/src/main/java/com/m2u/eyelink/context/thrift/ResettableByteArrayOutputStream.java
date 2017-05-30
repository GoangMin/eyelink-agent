package com.m2u.eyelink.context.thrift;

import java.io.ByteArrayOutputStream;

public class ResettableByteArrayOutputStream extends ByteArrayOutputStream {

    public ResettableByteArrayOutputStream(int size) {
        super(size);
    }
    
    public void reset(int resetIndex) {
        int bufferLength = buf.length;
        if (bufferLength < resetIndex) {
            throw new IllegalArgumentException("PushbackByteArrayOutputStream reset fail. current buffer length:" + bufferLength + ", resetIndex:" + resetIndex);
        }
        
        this.count = resetIndex;
    }
    
}
