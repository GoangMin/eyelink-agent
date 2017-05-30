package com.m2u.eyelink.context;

public interface AsyncTraceId extends TraceId {

    int getAsyncId();
    
    long getSpanStartTime();
    
    TraceId getParentTraceId();

    short nextAsyncSequence();
}
