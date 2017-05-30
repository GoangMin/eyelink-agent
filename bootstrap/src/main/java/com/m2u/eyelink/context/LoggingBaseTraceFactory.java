package com.m2u.eyelink.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.annotations.*;

public class LoggingBaseTraceFactory implements BaseTraceFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BaseTraceFactory baseTraceFactory;

    public static BaseTraceFactory wrap(BaseTraceFactory baseTraceFactory) {
        if (baseTraceFactory == null) {
            throw new NullPointerException("baseTraceFactory must not be null");
        }
        return new LoggingBaseTraceFactory(baseTraceFactory);
    }

    private LoggingBaseTraceFactory(BaseTraceFactory baseTraceFactory) {
        if (baseTraceFactory == null) {
            throw new NullPointerException("baseTraceFactory must not be null");
        }

        this.baseTraceFactory = baseTraceFactory;
    }

    @Override
    public Trace disableSampling() {
        logger.debug("disableSampling()");

        return baseTraceFactory.disableSampling();
    }

    @Override
    public Trace continueTraceObject(TraceId traceId) {
        if (logger.isDebugEnabled()) {
            logger.debug("continueTraceObject(traceId = [{}])", traceId);
        }

        return baseTraceFactory.continueTraceObject(traceId);
    }

    @Override
    public Trace continueTraceObject(Trace trace) {
        if (logger.isDebugEnabled()) {
            logger.debug("continueTraceObject(trace = [{}])", trace);
        }

        return baseTraceFactory.continueTraceObject(trace);
    }

    @Override
    @InterfaceAudience.LimitedPrivate("vert.x")
    public Trace continueAsyncTraceObject(TraceId traceId) {
        if (logger.isDebugEnabled()) {
            logger.debug("continueAsyncTraceObject(traceId = [{}])", traceId);
        }


        return baseTraceFactory.continueAsyncTraceObject(traceId);
    }

    @Override
    public Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("continueAsyncTraceObject(traceId = [{}], asyncId = [{}], startTime = [{}])", traceId, asyncId, startTime);
        }

        return baseTraceFactory.continueAsyncTraceObject(traceId, asyncId, startTime);
    }

    @Override
    public Trace newTraceObject() {
        logger.debug("newTraceObject()");

        return baseTraceFactory.newTraceObject();
    }

    @Override
    @InterfaceAudience.LimitedPrivate("vert.x")
    public Trace newAsyncTraceObject() {
        logger.debug("newAsyncTraceObject()");

        return baseTraceFactory.newAsyncTraceObject();
    }
}
