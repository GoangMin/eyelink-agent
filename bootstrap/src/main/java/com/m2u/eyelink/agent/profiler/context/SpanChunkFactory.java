package com.m2u.eyelink.agent.profiler.context;

import java.util.List;

import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.Span;
import com.m2u.eyelink.context.SpanEvent;

public class SpanChunkFactory {

    private final AgentInformation agentInformation;

    public SpanChunkFactory(AgentInformation agentInformation) {
        if (agentInformation == null) {
            throw new NullPointerException("agentInformation must not be null");
        }
        this.agentInformation = agentInformation;
    }

    public SpanChunk create(final List<SpanEvent> flushData) {
        if (flushData == null) {
            throw new NullPointerException("flushData must not be null");
        }
        // TODO must be equals to or greater than 1
        final int size = flushData.size();
        if (size < 1) {
            throw new IllegalArgumentException("flushData.size() < 1 size:" + size);
        }


        final SpanEvent first = flushData.get(0);
        if (first == null) {
            throw new IllegalStateException("first SpanEvent is null");
        }
        final Span parentSpan = first.getSpan();
        final String agentId = this.agentInformation.getAgentId();

        final SpanChunk spanChunk = new SpanChunk(flushData);
        spanChunk.setAgentId(agentId);
        spanChunk.setApplicationName(this.agentInformation.getApplicationName());
        spanChunk.setAgentStartTime(this.agentInformation.getStartTime());
        spanChunk.setApplicationServiceType(this.agentInformation.getServerType().getCode());

        spanChunk.setServiceType(parentSpan.getServiceType());


        final byte[] transactionId = parentSpan.getTransactionId();
        spanChunk.setTransactionId(transactionId);


        spanChunk.setSpanId(parentSpan.getSpanId());

        spanChunk.setEndPoint(parentSpan.getEndPoint());
        return spanChunk;
    }
}
