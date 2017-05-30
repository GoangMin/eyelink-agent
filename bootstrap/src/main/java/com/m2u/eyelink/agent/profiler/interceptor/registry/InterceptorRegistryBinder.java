package com.m2u.eyelink.agent.profiler.interceptor.registry;


public interface InterceptorRegistryBinder {

    void bind();

    void unbind();

    InterceptorRegistryAdaptor getInterceptorRegistryAdaptor();

    String getInterceptorRegistryClassName();
}

