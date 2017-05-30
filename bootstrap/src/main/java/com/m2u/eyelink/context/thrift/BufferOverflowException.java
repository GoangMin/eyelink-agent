package com.m2u.eyelink.context.thrift;

public class BufferOverflowException extends RuntimeException {

    public BufferOverflowException(String message) {
        super(message);
    }

    public BufferOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
