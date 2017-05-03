package com.m2u.eyelink.context;

import com.m2u.eyelink.annotations.*;

public class DefaultBaseTraceFactory implements BaseTraceFactory {

    private final TraceContext traceContext;

    private final StorageFactory storageFactory;
    private final Sampler sampler;

    private final IdGenerator idGenerator;

    public DefaultBaseTraceFactory(TraceContext traceContext, StorageFactory storageFactory, Sampler sampler, IdGenerator idGenerator) {
        if (traceContext == null) {
            throw new NullPointerException("traceContext must not be null");
        }
        if (storageFactory == null) {
            throw new NullPointerException("storageFactory must not be null");
        }
        if (sampler == null) {
            throw new NullPointerException("sampler must not be null");
        }
        if (idGenerator == null) {
            throw new NullPointerException("idGenerator must not be null");
        }
        this.traceContext = traceContext;
        this.storageFactory = storageFactory;
        this.sampler = sampler;
        this.idGenerator = idGenerator;
    }


    // continue to trace the request that has been determined to be sampled on previous nodes
    @Override
    public Trace continueTraceObject(final TraceId traceId) {
        // TODO need to modify how to bind a datasender
        // always set true because the decision of sampling has been  made on previous nodes
        // TODO need to consider as a target to sample in case Trace object has a sampling flag (true) marked on previous node.
        final boolean sampling = true;
        final Storage storage = storageFactory.createStorage();
        final long localTransactionId = this.idGenerator.nextContinuedTransactionId();

        final Trace trace = new DefaultTrace(traceContext, storage, traceId, localTransactionId, sampling);
        return trace;
    }


    @Override
    public Trace continueTraceObject(Trace trace) {
        return trace;
    }


    @Override
    public Trace newTraceObject() {
        // TODO need to modify how to inject a datasender
        final boolean sampling = sampler.isSampling();
        if (sampling) {
            final Storage storage = storageFactory.createStorage();

            final long localTransactionId = idGenerator.nextTransactionId();
            final TraceId traceId = new DefaultTraceId(traceContext.getAgentId(), traceContext.getAgentStartTime(), localTransactionId);
            final Trace trace = new DefaultTrace(traceContext, storage, traceId, localTransactionId, sampling);

            return trace;
        } else {
            return newDisableTrace();
        }
    }



    // internal async trace.
    @Override
    public Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime) {

        final TraceId parentTraceId = traceId.getParentTraceId();
        final boolean sampling = true;
        final Storage storage = storageFactory.createStorage();
        final Storage asyncStorage = new AsyncStorage(storage);
        final Trace trace = new DefaultTrace(traceContext, asyncStorage, parentTraceId, IdGenerator.UNTRACKED_ID, sampling);

        final AsyncTrace asyncTrace = new AsyncTrace(trace, asyncId, traceId.nextAsyncSequence(), startTime);

        return asyncTrace;
    }

    // entry point async trace.
    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace continueAsyncTraceObject(final TraceId traceId) {

        final boolean sampling = true;

        final Storage storage = storageFactory.createStorage();
        final long localTransactionId = this.idGenerator.nextContinuedTransactionId();
        final DefaultTrace trace = new DefaultTrace(traceContext, storage, traceId, localTransactionId, sampling);

        final SpanAsyncStateListener asyncStateListener = new SpanAsyncStateListener(trace.getSpan(), storageFactory.createStorage());
        final ListenableAsyncState stateListener = new ListenableAsyncState(asyncStateListener);
        final AsyncTrace asyncTrace = new AsyncTrace(trace, stateListener);

        return asyncTrace;
    }

    // entry point async trace.
    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace newAsyncTraceObject() {

        final boolean sampling = sampler.isSampling();
        if (sampling) {
            final Storage storage = storageFactory.createStorage();
            final long localTransactionId = idGenerator.nextTransactionId();
            final TraceId traceId = new DefaultTraceId(traceContext.getAgentId(), traceContext.getAgentStartTime(), localTransactionId);
            final DefaultTrace trace = new DefaultTrace(traceContext, storage, traceId, localTransactionId, sampling);

            final SpanAsyncStateListener asyncStateListener = new SpanAsyncStateListener(trace.getSpan(), storageFactory.createStorage());
            final AsyncState closer = new ListenableAsyncState(asyncStateListener);
            final AsyncTrace asyncTrace = new AsyncTrace(trace, closer);

            return asyncTrace;
        } else {
            return newDisableTrace();
        }
    }

    private Trace newDisableTrace() {
        final long nextDisabledId = this.idGenerator.nextDisabledId();
        final Trace disableTrace = new DisableTrace(nextDisabledId);
        return disableTrace;
    }

    @Override
    public Trace disableSampling() {
        final long nextContinuedDisabledId = this.idGenerator.nextContinuedDisabledId();
        final Trace trace = new DisableTrace(nextContinuedDisabledId);
        return trace;
    }

}