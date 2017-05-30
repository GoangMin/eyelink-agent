package com.m2u.eyelink.agent.interceptor;


public interface AroundInterceptor2 extends Interceptor {

    void before(Object target, Object arg0, Object arg1);

    void after(Object target, Object arg0, Object arg1, Object result, Throwable throwable);
}
