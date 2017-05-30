package com.m2u.eyelink.agent.profiler.interceptor.factory;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentMethod;
import com.m2u.eyelink.agent.interceptor.Interceptor;
import com.m2u.eyelink.agent.interceptor.scope.ExecutionPolicy;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;

public interface InterceptorFactory {
    Interceptor getInterceptor(ClassLoader classLoader, String interceptorClassName, Object[] providedArguments, InterceptorScope scope, ExecutionPolicy policy, InstrumentClass target, InstrumentMethod targetMethod);
}
