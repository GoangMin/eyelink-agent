package com.m2u.eyelink.agent.interceptor.scope;


public interface InterceptorScope {
    String getName();
    InterceptorScopeInvocation getCurrentInvocation();

}
