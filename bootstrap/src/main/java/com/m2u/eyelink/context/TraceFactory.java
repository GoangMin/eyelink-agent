package com.m2u.eyelink.context;


public interface TraceFactory extends BaseTraceFactory {

    Trace currentTraceObject();

    Trace currentRpcTraceObject();

    Trace currentRawTraceObject();

    Trace removeTraceObject();
}
