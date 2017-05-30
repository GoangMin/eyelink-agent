package com.m2u.eyelink.agent.interceptor;

import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.SpanEventRecorder;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.trace.ServiceType;

public class BasicMethodInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(BasicMethodInterceptor.class);
    private final boolean isDebug = logger.isDebugEnabled();

    private final MethodDescriptor descriptor;
    private final TraceContext traceContext;
    private final ServiceType serviceType;

    public BasicMethodInterceptor(TraceContext traceContext, MethodDescriptor descriptor, ServiceType serviceType) {
        this.descriptor = descriptor;
        this.traceContext = traceContext;
        this.serviceType = serviceType;
    }
    
    public BasicMethodInterceptor(TraceContext traceContext, MethodDescriptor descriptor) {
        this(traceContext, descriptor, ServiceType.INTERNAL_METHOD);
    }

    @Override
    public void before(Object target, Object[] args) {
        if (isDebug) {
            logger.beforeInterceptor(target, args);
        }

        Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        final SpanEventRecorder recorder = trace.traceBlockBegin();
        recorder.recordServiceType(serviceType);
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        if (isDebug) {
            logger.afterInterceptor(target, args);
        }

        Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        try {
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            recorder.recordApi(descriptor);
            recorder.recordException(throwable);
        } finally {
            trace.traceBlockEnd();
        }
    }
}