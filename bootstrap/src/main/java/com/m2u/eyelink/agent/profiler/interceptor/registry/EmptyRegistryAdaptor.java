package com.m2u.eyelink.agent.profiler.interceptor.registry;

import com.m2u.eyelink.agent.interceptor.Interceptor;
import com.m2u.eyelink.agent.interceptor.LoggingInterceptor;

public final class EmptyRegistryAdaptor implements InterceptorRegistryAdaptor {

    public static final InterceptorRegistryAdaptor EMPTY = new EmptyRegistryAdaptor();

    private static final LoggingInterceptor LOGGING_INTERCEPTOR = new LoggingInterceptor("com.navercorp.pinpoint.profiler.interceptor.EMPTY");

    public EmptyRegistryAdaptor() {
    }


    @Override
    public int addInterceptor(Interceptor interceptor) {
        return -1;
    }


    public Interceptor getInterceptor(int key) {
        return LOGGING_INTERCEPTOR;
    }
}
