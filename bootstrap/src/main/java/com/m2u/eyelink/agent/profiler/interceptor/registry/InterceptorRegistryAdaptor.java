package com.m2u.eyelink.agent.profiler.interceptor.registry;

import com.m2u.eyelink.agent.interceptor.Interceptor;

public interface InterceptorRegistryAdaptor {
    int addInterceptor(Interceptor interceptor);
    Interceptor getInterceptor(int key);
}
