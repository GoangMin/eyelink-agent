package com.m2u.eyelink.agent.interceptor.scope;

import com.m2u.eyelink.context.scope.PoolObjectFactory;
import com.m2u.eyelink.interceptor.scope.DefaultInterceptorScope;

public class InterceptorScopeFactory implements PoolObjectFactory<String, InterceptorScope> {
    @Override
    public InterceptorScope create(String scopeName) {
        return new DefaultInterceptorScope(scopeName);
    }
}
