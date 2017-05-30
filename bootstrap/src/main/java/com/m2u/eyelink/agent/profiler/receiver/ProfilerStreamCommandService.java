package com.m2u.eyelink.agent.profiler.receiver;

import org.apache.thrift.TBase;

import com.m2u.eyelink.sender.ServerStreamChannelContext;
import com.m2u.eyelink.sender.StreamCode;

public interface ProfilerStreamCommandService {
	StreamCode streamCommandService(TBase tBase, ServerStreamChannelContext streamChannelContext);
    
}
