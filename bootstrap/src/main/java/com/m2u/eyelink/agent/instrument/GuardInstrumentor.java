package com.m2u.eyelink.agent.instrument;

import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.config.ProfilerConfig;

public class GuardInstrumentor implements Instrumentor {
    private final InstrumentContext instrumentContext;
    private boolean closed = false;

    public GuardInstrumentor(InstrumentContext instrumentContext) {
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
        checkOpen();
        return instrumentContext.getInstrumentClass(classLoader, className, classfileBuffer);
    }

    @Override
    public boolean exist(ClassLoader classLoader, String className) {
        checkOpen();
        return instrumentContext.exist(classLoader, className);
    }

    @Override
    public InterceptorScope getInterceptorScope(String scopeName) {
        checkOpen();
        return instrumentContext.getInterceptorScope(scopeName);
    }

    @Override
    public <T> Class<? extends T> injectClass(ClassLoader targetClassLoader, String className) {
        checkOpen();
        return instrumentContext.injectClass(targetClassLoader, className);
    }

    @Override
    public void transform(ClassLoader classLoader, String targetClassName, TransformCallback transformCallback) {
        checkOpen();
        instrumentContext.addClassFileTransformer(classLoader, targetClassName, transformCallback);
    }

    @Override
    public void retransform(Class<?> target, TransformCallback transformCallback) {
        checkOpen();
        instrumentContext.retransform(target, transformCallback);
    }

    public void close() {
        this.closed = true;
    }

    private void checkOpen() {
        if (closed) {
            throw new IllegalStateException("Instrumentor already closed");
        }
    }
}
