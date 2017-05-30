package com.m2u.eyelink.agent.interceptor;


public interface AroundInterceptor1 extends Interceptor {

    void before(Object target, Object arg0);

    void after(Object target, Object arg0, Object result, Throwable throwable);
}
