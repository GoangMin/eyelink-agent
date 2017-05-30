package com.m2u.eyelink.context;

public interface Storage {

    /**
     *
     * @param spanEvent
     */
    void store(SpanEvent spanEvent);

    /**
     *
     * @param span
     */
    void store(Span span);

    void flush();

    void close();
}
