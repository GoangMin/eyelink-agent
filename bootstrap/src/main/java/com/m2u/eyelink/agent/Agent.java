package com.m2u.eyelink.agent;

import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.TraceContext;


public interface Agent {

    void start();

    void stop();
    
    TraceContext getTraceContext();

    ProfilerConfig getProfilerConfig();
}
