package com.m2u.eyelink.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.exception.ELAgentException;
import com.m2u.eyelink.annotations.*;

public class ThreadLocalTraceFactory implements TraceFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Binder<Trace> threadLocalBinder = new ThreadLocalBinder<Trace>();

    private final BaseTraceFactory baseTraceFactory;

    public ThreadLocalTraceFactory(BaseTraceFactory baseTraceFactory) {
        if (baseTraceFactory == null) {
            throw new NullPointerException("baseTraceFactory must not be null");
        }
        this.baseTraceFactory = baseTraceFactory;
    }


    /**
     * Return Trace object AFTER validating whether it can be sampled or not.
     *
     * @return Trace
     */
    @Override
    public Trace currentTraceObject() {
        final Trace trace = threadLocalBinder.get();
        if (trace == null) {
            return null;
        }
        if (trace.canSampled()) {
            return trace;
        }
        return null;
    }

    /**
     * Return Trace object without validating
     *
     * @return
     */
    @Override
    public Trace currentRpcTraceObject() {
        final Trace trace = threadLocalBinder.get();
        if (trace == null) {
            return null;
        }
        return trace;
    }

    @Override
    public Trace currentRawTraceObject() {
        return threadLocalBinder.get();
    }

    @Override
    public Trace disableSampling() {
        checkBeforeTraceObject();
        final Trace trace = this.baseTraceFactory.disableSampling();

        bind(trace);

        return trace;
    }

    // continue to trace the request that has been determined to be sampled on previous nodes
    @Override
    public Trace continueTraceObject(final TraceId traceId) {
        checkBeforeTraceObject();

        Trace trace = this.baseTraceFactory.continueTraceObject(traceId);

        bind(trace);
        return trace;
    }


    @Override
    public Trace continueTraceObject(Trace trace) {
        checkBeforeTraceObject();

        bind(trace);
        return trace;
    }

    private void checkBeforeTraceObject() {
        final Trace old = this.threadLocalBinder.get();
        if (old != null) {
            final ELAgentException exception = new ELAgentException("already Trace Object exist.");
            if (logger.isWarnEnabled()) {
                logger.warn("beforeTrace:{}", old, exception);
            }
            throw exception;
        }
    }

    @Override
    public Trace newTraceObject() {
        checkBeforeTraceObject();

        final Trace trace = this.baseTraceFactory.newTraceObject();

        bind(trace);
        return trace;
    }

    private void bind(Trace trace) {
        threadLocalBinder.set(trace);

//        // TODO traceChain example
//        Trace traceChain = new TraceChain(trace);
//        threadLocalBinder.set(traceChain);
//
//        // MetricTraceFactory
//        final Trace delegatedTrace = this.delegate.newTraceObject();
//        if (delegatedTrace instanceof TraceChain) {
//            TraceChain chain = (TraceChain)delegatedTrace;
//            TraceWrap metricTrace = new MetricTraceWrap();
//            // add metricTraceWrap to traceChain
//            chain.addFirst(metricTrace);
//            return chain;
//        } else {
//            logger.warn("error???");
//            return delegatedTrace;
//        }
    }

    @Override
    public Trace removeTraceObject() {
        return this.threadLocalBinder.remove();
    }

    // internal async trace.
    @Override
    public Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime) {
        checkBeforeTraceObject();

        final Trace trace = this.baseTraceFactory.continueAsyncTraceObject(traceId, asyncId, startTime);

        bind(trace);
        return trace;
    }

    // entry point async trace.
    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace continueAsyncTraceObject(final TraceId traceId) {
        checkBeforeTraceObject();

        final Trace trace = this.baseTraceFactory.continueAsyncTraceObject(traceId);

        bind(trace);
        return trace;
    }

    // entry point async trace.
    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace newAsyncTraceObject() {
        checkBeforeTraceObject();

        final Trace trace = this.baseTraceFactory.newAsyncTraceObject();

        bind(trace);
        return trace;
    }
}