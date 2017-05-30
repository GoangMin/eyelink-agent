package com.m2u.eyelink.agent.profiler.context;

import java.util.List;

import com.m2u.eyelink.context.SpanEvent;
import com.m2u.eyelink.context.thrift.TSpanChunk;

public class SpanChunk extends TSpanChunk {

    public SpanChunk(List<SpanEvent> spanEventList) {
        if (spanEventList == null) {
            throw new NullPointerException("spanEventList must not be null");
        }
        setSpanEventList((List) spanEventList);
    }
}
