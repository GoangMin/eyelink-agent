package com.m2u.eyelink.agent.profiler.receiver;

import org.apache.thrift.TBase;

public interface ProfilerRequestCommandService extends ProfilerCommandService {
    TBase<?, ?> requestCommandService(TBase tBase);

}
