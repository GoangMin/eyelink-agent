package com.m2u.eyelink.agent.interceptor;


public interface ApiIdAwareAroundInterceptor extends Interceptor {
    void before(Object target, int apiId, Object[] args);
    void after(Object target, int apiId, Object[] args, Object result, Throwable throwable);
}
