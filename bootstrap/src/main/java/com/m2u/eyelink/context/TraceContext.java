package com.m2u.eyelink.context;

import com.m2u.eyelink.annotations.InterfaceAudience;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.plugin.monitor.PluginMonitorContext;

public interface TraceContext {
    Trace currentTraceObject();

    /**
     * return a trace whose sampling rate should be further verified
     * 
     * @return
     */
    Trace currentRawTraceObject();

    Trace continueTraceObject(TraceId traceId);

    Trace continueTraceObject(Trace trace);

    Trace newTraceObject();

    /**
     * internal experimental api
     */
    @InterfaceAudience.LimitedPrivate("vert.x")
    Trace newAsyncTraceObject();

    /**
     * internal experimental api
     */
    @InterfaceAudience.LimitedPrivate("vert.x")
    Trace continueAsyncTraceObject(TraceId traceId);

    Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime);

    Trace removeTraceObject();

    // ActiveThreadCounter getActiveThreadCounter();

    String getAgentId();

    String getApplicationName();

    long getAgentStartTime();

    short getServerTypeCode();

    String getServerType();

    int cacheApi(MethodDescriptor methodDescriptor);

    int cacheString(String value);

    // TODO extract jdbc related methods
    ParsingResult parseSql(String sql);

    boolean cacheSql(ParsingResult parsingResult);

    TraceId createTraceId(String transactionId, long parentSpanID, long spanID, short flags);

    Trace disableSampling();

    ProfilerConfig getProfilerConfig();

    ServerMetaDataHolder getServerMetaDataHolder();

    int getAsyncId();

    PluginMonitorContext getPluginMonitorContext();
}
