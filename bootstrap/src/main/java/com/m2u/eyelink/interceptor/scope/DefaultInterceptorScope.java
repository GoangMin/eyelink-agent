package com.m2u.eyelink.interceptor.scope;

import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScopeInvocation;

public class DefaultInterceptorScope implements InterceptorScope {
    private final String name;
    private final ThreadLocal<InterceptorScopeInvocation> threadLocal;
    
    public DefaultInterceptorScope(final String name) {
        this.name = name;
        this.threadLocal = new ThreadLocal<InterceptorScopeInvocation>() {

            @Override
            protected InterceptorScopeInvocation initialValue() {
                return new DefaultInterceptorScopeInvocation(name);
            }
            
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InterceptorScopeInvocation getCurrentInvocation() {
        return threadLocal.get();
    }
}
