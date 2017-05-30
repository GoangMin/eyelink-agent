package com.m2u.eyelink.agent.profiler.interceptor;

import java.lang.reflect.Method;

import com.m2u.eyelink.agent.interceptor.Interceptor;

public interface InterceptorDefinition {
    Class<? extends Interceptor> getInterceptorBaseClass();

    Class<? extends Interceptor> getInterceptorClass();

    InterceptorType getInterceptorType();

    CaptureType getCaptureType();

    Method getBeforeMethod();

    Method getAfterMethod();
}
