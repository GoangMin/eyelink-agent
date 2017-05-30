package com.m2u.eyelink.thrift;

public interface ResettableOutputStream {
    void mark();

    void resetToMarkIndex();
}
