package com.m2u.eyelink.agent.profiler.receiver;

import java.util.Set;

import org.apache.thrift.TBase;

public interface ProfilerCommandServiceLocator {

    ProfilerCommandService getService(TBase tBase);

    ProfilerSimpleCommandService getSimpleService(TBase tBase);

    ProfilerRequestCommandService getRequestService(TBase tBase);

    ProfilerStreamCommandService getStreamService(TBase tBase);

    Set<Class<? extends TBase>> getCommandServiceClasses();

    Set<Short> getCommandServiceCodes();

}
