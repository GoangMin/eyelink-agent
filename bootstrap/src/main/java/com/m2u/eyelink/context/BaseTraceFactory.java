package com.m2u.eyelink.context;

import com.m2u.eyelink.annotations.InterfaceAudience;

public interface BaseTraceFactory {
    Trace disableSampling();

    // picked as sampling target at remote
    Trace continueTraceObject(TraceId traceId);

    Trace continueTraceObject(Trace trace);

    @InterfaceAudience.LimitedPrivate("vert.x")
    Trace continueAsyncTraceObject(TraceId traceId);

    Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime);

    Trace newTraceObject();

    @InterfaceAudience.LimitedPrivate("vert.x")
    Trace newAsyncTraceObject();
}
