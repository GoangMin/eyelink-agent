package com.m2u.eyelink.agent.profiler.monitor;

public interface CounterMonitor {
    void incr();

    void incr(final long delta);

    void decr();

    void decr(final long delta);

    void reset();

    long getCount();

}
