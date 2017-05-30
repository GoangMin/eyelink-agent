package com.m2u.eyelink.agent.profiler.receiver.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TBase;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory;
import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory.ActiveTraceHistogram;
import com.m2u.eyelink.agent.profiler.receiver.CommandSerializer;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerRequestCommandService;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerStreamCommandService;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.context.thrift.SerializationUtils;
import com.m2u.eyelink.context.thrift.TCmdActiveThreadCount;
import com.m2u.eyelink.context.thrift.TCmdActiveThreadCountRes;
import com.m2u.eyelink.rpc.StreamChannelStateChangeEventHandler;
import com.m2u.eyelink.rpc.stream.ServerStreamChannel;
import com.m2u.eyelink.rpc.util.TimerFactory;
import com.m2u.eyelink.sender.ServerStreamChannelContext;
import com.m2u.eyelink.sender.StreamChannelStateCode;
import com.m2u.eyelink.sender.StreamCode;

public class ActiveThreadCountService implements ProfilerRequestCommandService, ProfilerStreamCommandService {

    private static final long DEFAULT_FLUSH_DELAY = 1000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Object lock = new Object();

    private final StreamChannelStateChangeEventHandler stateChangeEventHandler = new ActiveThreadCountStreamChannelStateChangeEventHandler();
    private final HashedWheelTimer timer = TimerFactory.createHashedWheelTimer("ActiveThreadCountService-Timer", 100, TimeUnit.MILLISECONDS, 512);
    private final long flushDelay;
    private final AtomicBoolean onTimerTask = new AtomicBoolean(false);

    private final List<ServerStreamChannel> streamChannelRepository = new CopyOnWriteArrayList<ServerStreamChannel>();

    private final ActiveTraceHistogramFactory activeTraceHistogramFactory;

    public ActiveThreadCountService(ActiveTraceLocator activeTraceLocator) {
        this(activeTraceLocator, DEFAULT_FLUSH_DELAY);
    }

    public ActiveThreadCountService(ActiveTraceLocator activeTraceLocator, long flushDelay) {
        if (activeTraceLocator == null) {
            throw new NullPointerException("activeTraceLocator");
        }
        this.activeTraceHistogramFactory = new ActiveTraceHistogramFactory(activeTraceLocator);
        this.flushDelay = flushDelay;
    }

    @Override
    public Class<? extends TBase> getCommandClazz() {
        return TCmdActiveThreadCount.class;
    }

    @Override
    public TBase<?, ?> requestCommandService(TBase activeThreadCountObject) {
        if (activeThreadCountObject == null) {
            throw new NullPointerException("activeThreadCountObject may not be null.");
        }

        return getActiveThreadCountResponse();
    }

    @Override
    public StreamCode streamCommandService(TBase tBase, ServerStreamChannelContext streamChannelContext) {
        logger.info("streamCommandService object:{}, streamChannelContext:{}", tBase, streamChannelContext);
        streamChannelContext.getStreamChannel().addStateChangeEventHandler(stateChangeEventHandler);
        return StreamCode.OK;
    }

    private TCmdActiveThreadCountRes getActiveThreadCountResponse() {
        ActiveTraceHistogram activeTraceHistogram = this.activeTraceHistogramFactory.createHistogram();

        TCmdActiveThreadCountRes response = new TCmdActiveThreadCountRes();
        response.setHistogramSchemaType(activeTraceHistogram.getHistogramSchema().getTypeCode());
        response.setActiveThreadCount(activeTraceHistogram.getActiveTraceCounts());
        response.setTimeStamp(System.currentTimeMillis());

        return response;
    }

    private class ActiveThreadCountStreamChannelStateChangeEventHandler implements StreamChannelStateChangeEventHandler<ServerStreamChannel> {

        @Override
        public void eventPerformed(ServerStreamChannel streamChannel, StreamChannelStateCode updatedStateCode) throws Exception {
            logger.info("eventPerformed. ServerStreamChannel:{}, StreamChannelStateCode:{}.", streamChannel, updatedStateCode);
            synchronized (lock) {
                switch (updatedStateCode) {
                    case CONNECTED:
                        streamChannelRepository.add(streamChannel);
                        boolean turnOn = onTimerTask.compareAndSet(false, true);
                        if (turnOn) {
                            logger.info("turn on ActiveThreadCountTimerTask.");
                            timer.newTimeout(new ActiveThreadCountTimerTask(), flushDelay, TimeUnit.MILLISECONDS);
                        }
                        break;
                    case CLOSED:
                    case ILLEGAL_STATE:
                        boolean removed = streamChannelRepository.remove(streamChannel);
                        if (removed && streamChannelRepository.isEmpty()) {
                            boolean turnOff = onTimerTask.compareAndSet(true, false);
                            if (turnOff) {
                                logger.info("turn off ActiveThreadCountTimerTask.");
                            }
                        }
                        break;
                }
            }
        }

        @Override
        public void exceptionCaught(ServerStreamChannel streamChannel, StreamChannelStateCode updatedStateCode, Throwable e) {
            logger.warn("exceptionCaught caused:{}. ServerStreamChannel:{}, StreamChannelStateCode:{}.", e.getMessage(), streamChannel, updatedStateCode, e);
        }

    }

    private class ActiveThreadCountTimerTask implements TimerTask {

        @Override
        public void run(Timeout timeout) throws Exception {
            logger.debug("ActiveThreadCountTimerTask started. target-streams:{}", streamChannelRepository);

            try {
                TCmdActiveThreadCountRes activeThreadCountResponse = getActiveThreadCountResponse();
                for (ServerStreamChannel serverStreamChannel : streamChannelRepository) {
                    byte[] payload = SerializationUtils.serialize(activeThreadCountResponse, CommandSerializer.SERIALIZER_FACTORY, null);
                    if (payload != null) {
                        serverStreamChannel.sendData(payload);
                    }
                }
            } finally {
                if (timer != null && onTimerTask.get()) {
                    timer.newTimeout(this, flushDelay, TimeUnit.MILLISECONDS);
                }
            }
        }

    }
}