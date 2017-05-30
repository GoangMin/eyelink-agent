package com.m2u.eyelink.agent.profiler.receiver;

import org.apache.thrift.TBase;

public interface ProfilerSimpleCommandService extends ProfilerCommandService {

    void simpleCommandService(TBase<?, ?> tbase);

}
