package com.m2u.eyelink.agent.profiler.monitor;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;


public interface AgentStatCollector<T extends TBase<T, ? extends TFieldIdEnum>> {
    
    T collect();

}
