package com.m2u.eyelink.agent.instrument;

import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.config.ProfilerConfig;

public class InstrumentorDelegate implements Instrumentor {
    private final InstrumentContext instrumentContext;

    public InstrumentorDelegate(InstrumentContext instrumentContext) {
        if (instrumentContext == null) {
            throw new NullPointerException("instrumentContext must not be null");
        }
        this.instrumentContext = instrumentContext;
    }

    @Override
    public ProfilerConfig getProfilerConfig() {
        return instrumentContext.getTraceContext().getProfilerConfig();
    }

    @Override
    public InstrumentClass getInstrumentClass(ClassLoader classLoader, String className, byte[] classfileBuffer) {
        return instrumentContext.getInstrumentClass(classLoader, className, classfileBuffer);
    }

    @Override
    public boolean exist(ClassLoader classLoader, String className) {
        return instrumentContext.exist(classLoader, className);
    }

    @Override
    public InterceptorScope getInterceptorScope(String scopeName) {
        return instrumentContext.getInterceptorScope(scopeName);
    }

    @Override
    public <T> Class<? extends T> injectClass(ClassLoader targetClassLoader, String className) {
        return instrumentContext.injectClass(targetClassLoader, className);
    }

    @Override
    public void transform(ClassLoader classLoader, String targetClassName, TransformCallback transformCallback) {
        instrumentContext.addClassFileTransformer(classLoader, targetClassName, transformCallback);
    }

    @Override
    public void retransform(Class<?> target, TransformCallback transformCallback) {
        instrumentContext.retransform(target, transformCallback);
    }

}
