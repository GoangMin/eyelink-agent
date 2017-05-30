package com.m2u.eyelink.agent.instrument;

import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.config.ProfilerConfig;

public interface Instrumentor {

    ProfilerConfig getProfilerConfig();
    
    InstrumentClass getInstrumentClass(ClassLoader classLoader, String className, byte[] classfileBuffer);
    
    boolean exist(ClassLoader classLoader, String className);
    
    InterceptorScope getInterceptorScope(String scopeName);
        
    <T> Class<? extends T> injectClass(ClassLoader targetClassLoader, String className);
    
    void transform(ClassLoader classLoader, String targetClassName, TransformCallback transformCallback);
    
    void retransform(Class<?> target, TransformCallback transformCallback);
}
