package com.m2u.eyelink.agent.profiler.context.storage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.context.SpanChunk;
import com.m2u.eyelink.agent.profiler.context.SpanChunkFactory;
import com.m2u.eyelink.context.Span;
import com.m2u.eyelink.context.SpanEvent;
import com.m2u.eyelink.context.Storage;
import com.m2u.eyelink.sender.DataSender;

public class BufferedStorage implements Storage {
    private static final Logger logger = LoggerFactory.getLogger(BufferedStorage.class);
    private static final boolean isDebug = logger.isDebugEnabled();

    private static final int DEFAULT_BUFFER_SIZE = 20;

    private final int bufferSize;

    private List<SpanEvent> storage;
    private final DataSender dataSender;
    private final SpanChunkFactory spanChunkFactory;

    public BufferedStorage(DataSender dataSender, SpanChunkFactory spanChunkFactory) {
        this(dataSender, spanChunkFactory, DEFAULT_BUFFER_SIZE);
    }

    public BufferedStorage(DataSender dataSender, SpanChunkFactory spanChunkFactory, int bufferSize) {
        if (dataSender == null) {
            throw new NullPointerException("dataSender must not be null");
        }
        if (spanChunkFactory == null) {
            throw new NullPointerException("spanChunkFactory must not be null");
        }
        this.dataSender = dataSender;
        this.spanChunkFactory = spanChunkFactory;
        this.bufferSize = bufferSize;
        this.storage = new ArrayList<SpanEvent>(bufferSize);
    }

    @Override
    public void store(SpanEvent spanEvent) {
        List<SpanEvent> flushData = null;
        storage.add(spanEvent);
        if (storage.size() >= bufferSize) {
            // data copy
            flushData = storage;
            storage = new ArrayList<SpanEvent>(bufferSize);
        }

        if (flushData != null) {
            final SpanChunk spanChunk = spanChunkFactory.create(flushData);
            if (isDebug) {
                logger.debug("[BufferedStorage] Flush span-chunk {}", spanChunk);
            }
            dataSender.send(spanChunk);
        }
    }

    @Override
    public void store(Span span) {
        List<SpanEvent> spanEventList;
        spanEventList = storage;
        this.storage = new ArrayList<SpanEvent>(bufferSize);

        if (spanEventList != null && !spanEventList.isEmpty()) {
            span.setSpanEventList((List) spanEventList);
        }
        dataSender.send(span);

        if (isDebug) {
            logger.debug("[BufferedStorage] Flush span {}", span);
        }
    }

    public void flush() {
        List<SpanEvent> spanEventList;
        spanEventList = storage;
        this.storage = new ArrayList<SpanEvent>(bufferSize);

        if (spanEventList != null && !spanEventList.isEmpty()) {
            final SpanChunk spanChunk = spanChunkFactory.create(spanEventList);
            dataSender.send(spanChunk);
            if (isDebug) {
                logger.debug("flush span chunk {}", spanChunk);
            }
        }
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        return "BufferedStorage{" + "bufferSize=" + bufferSize + ", dataSender=" + dataSender + '}';
    }
}