package com.m2u.eyelink.agent.profiler.monitor;

import com.m2u.eyelink.context.thrift.TJvmGc;


public interface GarbageCollector extends AgentStatCollector<TJvmGc> {

    int getTypeCode();

}
