package com.m2u.eyelink.agent.profiler.receiver;

import org.apache.thrift.TBase;

public interface ProfilerCommandService {
    Class<? extends TBase> getCommandClazz();

}
