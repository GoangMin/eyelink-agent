package com.m2u.eyelink.agent.profiler.monitor;

public interface EventRateMonitor {
    void event();

    void events(final long count);

    long getCount();

    double getRate();

}
