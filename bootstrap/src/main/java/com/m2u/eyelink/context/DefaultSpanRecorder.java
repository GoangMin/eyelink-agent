package com.m2u.eyelink.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.trace.LoggingInfo;
import com.m2u.eyelink.trace.ServiceType;

public class DefaultSpanRecorder extends AbstractRecorder implements SpanRecorder {
    private final Logger logger = LoggerFactory.getLogger(DefaultTrace.class.getName());
    private final boolean isDebug = logger.isDebugEnabled();
    
    private final Span span;
    private final TraceId traceId;
    private final boolean sampling;
    
    public DefaultSpanRecorder(final TraceContext traceContext, final Span span, final TraceId traceId, final boolean sampling) {
        super(traceContext);

        this.span = span;
        this.traceId = traceId;
        this.sampling = sampling;
    }

    public Span getSpan() {
        return span;
    }
    
    public void recordTraceId(TraceId traceId) {
        span.recordTraceId(traceId);
    }

    @Override
    public void recordStartTime(long startTime) {
        span.setStartTime(startTime);
    }

    @Override
    void setExceptionInfo(int exceptionClassId, String exceptionMessage) {
        setExceptionInfo(true, exceptionClassId, exceptionMessage);
    }

    @Override
    void setExceptionInfo(boolean markError, int exceptionClassId, String exceptionMessage) {
        span.setExceptionInfo(exceptionClassId, exceptionMessage);
        if (markError) {
            if (!span.isSetErrCode()) {
                span.setErrCode(1);
            }
        }
    }

    @Override
    public void recordApiId(int apiId) {
        setApiId0(apiId);
    }

    void setApiId0(final int apiId) {
        span.setApiId(apiId);
    }

    @Override
    void addAnnotation(Annotation annotation) {
        span.addAnnotation(annotation);
    }

    @Override
    public void recordServiceType(ServiceType serviceType) {
        span.setServiceType(serviceType.getCode());
    }

    @Override
    public void recordRpcName(String rpc) {
        span.setRpc(rpc);
    }

    @Override
    public void recordRemoteAddress(String remoteAddress) {
        span.setRemoteAddr(remoteAddress);
    }

    @Override
    public void recordEndPoint(String endPoint) {
        span.setEndPoint(endPoint);
    }

    @Override
    public void recordParentApplication(String parentApplicationName, short parentApplicationType) {
            span.setParentApplicationName(parentApplicationName);
            span.setParentApplicationType(parentApplicationType);
            if (isDebug) {
                logger.debug("ParentApplicationName marked. parentApplicationName={}", parentApplicationName);
            }
    }

    @Override
    public void recordAcceptorHost(String host) {
        span.setAcceptorHost(host); // me
        if (isDebug) {
            logger.debug("Acceptor host received. host={}", host);
        }
    }

    @Override
    public boolean canSampled() {
        return sampling;
    }

    @Override
    public boolean isRoot() {
        return traceId.isRoot();
    }
    
    @Override
    public void recordLogging(LoggingInfo loggingInfo) {
        if (!span.isSetLoggingTransactionInfo()) {
            span.setLoggingTransactionInfo(loggingInfo.getCode()); 
        }
    }
    
    @Override
    public void recordTime(boolean time) {
        span.setTimeRecording(time);
        if(time) {
            if(!span.isSetStartTime()) {
                span.markBeforeTime();
            }
        } else {
            span.setElapsed(0);
            span.setElapsedIsSet(false);
            span.setStartTime(0);
            span.setStartTimeIsSet(false);
        }
    }


    @Override
    public Object attachFrameObject(Object frameObject) {
        return span.attachFrameObject(frameObject);
    }

    @Override
    public Object getFrameObject() {
        return span.getFrameObject();
    }

    @Override
    public Object detachFrameObject() {
        return span.detachFrameObject();
    }
}