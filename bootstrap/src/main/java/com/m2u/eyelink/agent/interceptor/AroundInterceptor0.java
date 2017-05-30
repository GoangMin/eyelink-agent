package com.m2u.eyelink.agent.interceptor;


public interface AroundInterceptor0 extends Interceptor {

    void before(Object target);

    void after(Object target, Object result, Throwable throwable);
}
