package com.m2u.eyelink.agent.profiler.sender;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TBase;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.context.thrift.HeaderTBaseDeserializer;
import com.m2u.eyelink.context.thrift.HeaderTBaseDeserializerFactory;
import com.m2u.eyelink.context.thrift.HeaderTBaseSerializer;
import com.m2u.eyelink.context.thrift.HeaderTBaseSerializerFactory;
import com.m2u.eyelink.context.thrift.TResult;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.util.TimerFactory;
import com.m2u.eyelink.sender.ELAgentClientReconnectEventListener;
import com.m2u.eyelink.sender.EnhancedDataSender;
import com.m2u.eyelink.sender.FutureListener;

public class TcpDataSender extends AbstractDataSender implements EnhancedDataSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    static {
        // preClassLoad
        ChannelBuffers.buffer(2);
    }

    private final ELAgentClient client;
    private final Timer timer;
    
    private final AtomicBoolean fireState = new AtomicBoolean(false);

    private final WriteFailFutureListener writeFailFutureListener;


    private final HeaderTBaseSerializer serializer = HeaderTBaseSerializerFactory.DEFAULT_FACTORY.createSerializer();

    private final RetryQueue retryQueue = new RetryQueue();

    private AsyncQueueingExecutor<Object> executor;

    public TcpDataSender(ELAgentClient client) {
        this.client = client;
        this.timer = createTimer();
        writeFailFutureListener = new WriteFailFutureListener(logger, "io write fail.", "host", -1);
        this.executor = createAsyncQueueingExecutor(1024 * 5, "Pinpoint-TcpDataExecutor");
    }
    
    private Timer createTimer() {
        HashedWheelTimer timer = TimerFactory.createHashedWheelTimer("Pinpoint-DataSender-Timer", 100, TimeUnit.MILLISECONDS, 512);
        timer.start();
        return timer;
    }
    
    @Override
    public boolean send(TBase<?, ?> data) {
        return executor.execute(data);
    }

    @Override
    public boolean request(TBase<?, ?> data) {
        return this.request(data, 3);
    }

    @Override
    public boolean request(TBase<?, ?> data, int retryCount) {
        RequestMarker message = new RequestMarker(data, retryCount);
        return executor.execute(message);
    }

    @Override
    public boolean request(TBase<?, ?> data, FutureListener<ResponseMessage> listener) {
        RequestMarker message = new RequestMarker(data, listener);
        return executor.execute(message);
    }

    @Override
    public boolean addReconnectEventListener(ELAgentClientReconnectEventListener eventListener) {
        return this.client.addPinpointClientReconnectEventListener(eventListener);
    }

    @Override
    public boolean removeReconnectEventListener(ELAgentClientReconnectEventListener eventListener) {
        return this.client.removePinpointClientReconnectEventListener(eventListener);
    }

    @Override
    public void stop() {
        executor.stop();

        Set<Timeout> stop = timer.stop();
        if (!stop.isEmpty()) {
            logger.info("stop Timeout:{}", stop.size());
        }
    }

    @Override
    protected void sendPacket(Object message) {
        try {
            if (message instanceof TBase) {
                byte[] copy = serialize(serializer, (TBase) message);
                if (copy == null) {
                    return;
                }
                doSend(copy);
            } else if (message instanceof RequestMarker) {
                RequestMarker requestMarker = (RequestMarker) message;

                TBase tBase = requestMarker.getTBase();
                int retryCount = requestMarker.getRetryCount();
                FutureListener futureListener = requestMarker.getFutureListener();
                byte[] copy = serialize(serializer, tBase);
                if (copy == null) {
                    return;
                }

                if (futureListener != null) {
                    doRequest(copy, futureListener);
                } else {
                    doRequest(copy, retryCount, tBase);
                }
            } else {
                logger.error("sendPacket fail. invalid dto type:{}", message.getClass());
                return;
            }
        } catch (Exception e) {
            logger.warn("tcp send fail. Caused:{}", e.getMessage(), e);
        }
    }

    private void doSend(byte[] copy) {
        Future write = this.client.sendAsync(copy);
        write.setListener(writeFailFutureListener);
    }

    // Separate doRequest method to avoid creating unnecessary objects. (Generally, sending message is successed when firt attempt.)
    private void doRequest(final byte[] requestPacket, final int maxRetryCount, final Object targetClass) {
        FutureListener futureListener = (new FutureListener<ResponseMessage>() {
            @Override
            public void onComplete(Future<ResponseMessage> future) {
                if (future.isSuccess()) {
                    // Should cache?
                    HeaderTBaseDeserializer deserializer = HeaderTBaseDeserializerFactory.DEFAULT_FACTORY.createDeserializer();
                    TBase<?, ?> response = deserialize(deserializer, future.getResult());
                    if (response instanceof TResult) {
                        TResult result = (TResult) response;
                        if (result.isSuccess()) {
                            logger.debug("result success");
                        } else {
                            logger.info("request fail. request:{} Caused:{}", targetClass, result.getMessage());
                            RetryMessage retryMessage = new RetryMessage(1, maxRetryCount, requestPacket, targetClass.getClass().getSimpleName());
                            retryRequest(retryMessage);
                        }
                    } else {
                        logger.warn("Invalid respose:{}", response);
                        // This is not retransmission. need to log for debugging
                        // it could be null
//                        retryRequest(requestPacket);
                    }
                } else {
                    logger.info("request fail. request:{} Caused:{}", targetClass, future.getCause().getMessage(), future.getCause());
                    RetryMessage retryMessage = new RetryMessage(1, maxRetryCount, requestPacket, targetClass.getClass().getSimpleName());
                    retryRequest(retryMessage);
                }
            }
        });

        doRequest(requestPacket, futureListener);
    }

    // Separate doRequest method to avoid creating unnecessary objects. (Generally, sending message is successed when firt attempt.)
    private void doRequest(final RetryMessage retryMessage) {
        FutureListener futureListener = (new FutureListener<ResponseMessage>() {
            @Override
            public void onComplete(Future<ResponseMessage> future) {
                if (future.isSuccess()) {
                    // Should cache?
                    HeaderTBaseDeserializer deserializer = HeaderTBaseDeserializerFactory.DEFAULT_FACTORY.createDeserializer();
                    TBase<?, ?> response = deserialize(deserializer, future.getResult());
                    if (response instanceof TResult) {
                        TResult result = (TResult) response;
                        if (result.isSuccess()) {
                            logger.debug("result success");
                        } else {
                            logger.info("request fail. request:{}, Caused:{}", retryMessage, result.getMessage());
                            retryRequest(retryMessage);
                        }
                    } else {
                        logger.warn("Invalid response:{}", response);
                        // This is not retransmission. need to log for debugging
                        // it could be null
//                        retryRequest(requestPacket);
                    }
                } else {
                    logger.info("request fail. request:{}, caused:{}", retryMessage, future.getCause().getMessage(), future.getCause());
                    retryRequest(retryMessage);
                }
            }
        });

        doRequest(retryMessage.getBytes(), futureListener);
    }

    private void retryRequest(RetryMessage retryMessage) {
        retryQueue.add(retryMessage);
        if (fireTimeout()) {
            timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    while(true) {
                        RetryMessage retryMessage = retryQueue.get();
                        if (retryMessage == null) {
                            // Maybe concurrency issue. But ignore it because it's unlikely.
                            fireComplete();
                            return;
                        }
                        int fail = retryMessage.fail();
                        doRequest(retryMessage);
                    }
                }
            }, 1000 * 10, TimeUnit.MILLISECONDS);
        }
    }

    private void doRequest(final byte[] requestPacket, FutureListener futureListener) {
        final Future<ResponseMessage> response = this.client.request(requestPacket);
        response.setListener(futureListener);
    }

    private boolean fireTimeout() {
        if (fireState.compareAndSet(false, true)) {
            return true;
        } else {
            return false;
        }
    }

    private void fireComplete() {
        logger.debug("fireComplete");
        fireState.compareAndSet(true, false);
    }

}
