package com.m2u.eyelink.agent.profiler.monitor;

public interface HistogramMonitor {
    void reset();

    void update(final long value);

    long getCount();

}
