package com.m2u.eyelink.rpc.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HeapByteBufferFactory implements ByteBufferFactory {

    @Override
    public ByteBuffer getBuffer(int capacity) {
        return getBuffer(DEFAULT_BYTE_ORDER, capacity);
    }

    @Override
    public ByteBuffer getBuffer(ByteOrder endianness, int capacity) {
        return ByteBuffer.allocate(capacity).order(endianness);
    }

}
