package com.m2u.eyelink.agent;

import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.TraceContext;

public class DummyAgent implements Agent {

    public DummyAgent(AgentOption option) {

    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public TraceContext getTraceContext() {
        return null;
    }

    @Override
     public ProfilerConfig getProfilerConfig() {
        return null;
    }

}
