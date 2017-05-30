package com.m2u.eyelink.rpc.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ChannelWriteFailListenableFuture;
import com.m2u.eyelink.rpc.ELAgentSocketException;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.rpc.packet.ResponsePacket;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.sender.DefaultFuture;
import com.m2u.eyelink.sender.FailureEventHandler;

public class RequestManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicInteger requestId = new AtomicInteger(1);

    private final ConcurrentMap<Integer, DefaultFuture<ResponseMessage>> requestMap = new ConcurrentHashMap<Integer, DefaultFuture<ResponseMessage>>();
    // Have to move Timer into factory?
    private final Timer timer;
    private final long defaultTimeoutMillis;

    public RequestManager(Timer timer, long defaultTimeoutMillis) {
        if (timer == null) {
            throw new NullPointerException("timer must not be null");
        }
        
        if (defaultTimeoutMillis <= 0) {
            throw new IllegalArgumentException("defaultTimeoutMillis must greater than zero.");
        }
        
        this.timer = timer;
        this.defaultTimeoutMillis = defaultTimeoutMillis;
    }

    private FailureEventHandler createFailureEventHandler(final int requestId) {
        FailureEventHandler failureEventHandler = new FailureEventHandler() {
            @Override
            public boolean fireFailure() {
                DefaultFuture<ResponseMessage> future = removeMessageFuture(requestId);
                if (future != null) {
                    // removed perfectly.
                    return true;
                }
                return false;
            }
        };
        return failureEventHandler;
    }


    private void addTimeoutTask(long timeoutMillis, DefaultFuture future) {
        if (future == null) {
            throw new NullPointerException("future");
        }
        try {
            Timeout timeout = timer.newTimeout(future, timeoutMillis, TimeUnit.MILLISECONDS);
            future.setTimeout(timeout);
        } catch (IllegalStateException e) {
            // this case is that timer has been shutdown. That maybe just means that socket has been closed.
            future.setFailure(new ELAgentSocketException("socket closed")) ;
        }
    }

    private int getNextRequestId() {
        return this.requestId.getAndIncrement();
    }

    public void messageReceived(ResponsePacket responsePacket, String objectUniqName) {
        final int requestId = responsePacket.getRequestId();
        final DefaultFuture<ResponseMessage> future = removeMessageFuture(requestId);
        if (future == null) {
            logger.warn("future not found:{}, objectUniqName:{}", responsePacket, objectUniqName);
            return;
        } else {
            logger.debug("responsePacket arrived packet:{}, objectUniqName:{}", responsePacket, objectUniqName);
        }

        ResponseMessage response = new ResponseMessage();
        response.setMessage(responsePacket.getPayload());
        future.setResult(response);
    }

    public void messageReceived(ResponsePacket responsePacket, ELAgentServer pinpointServer) {
        final int requestId = responsePacket.getRequestId();
        final DefaultFuture<ResponseMessage> future = removeMessageFuture(requestId);
        if (future == null) {
            logger.warn("future not found:{}, pinpointServer:{}", responsePacket, pinpointServer);
            return;
        } else {
            logger.debug("responsePacket arrived packet:{}, pinpointServer:{}", responsePacket, pinpointServer);
        }

        ResponseMessage response = new ResponseMessage();
        response.setMessage(responsePacket.getPayload());
        future.setResult(response);
    }

    public DefaultFuture<ResponseMessage> removeMessageFuture(int requestId) {
        return this.requestMap.remove(requestId);
    }

    public void messageReceived(RequestPacket requestPacket, Channel channel) {
        logger.error("unexpectedMessage received:{} address:{}", requestPacket, channel.getRemoteAddress());
    }

    public ChannelWriteFailListenableFuture<ResponseMessage> register(RequestPacket requestPacket) {
        return register(requestPacket, defaultTimeoutMillis);
    }

    public ChannelWriteFailListenableFuture<ResponseMessage> register(RequestPacket requestPacket, long timeoutMillis) {
        // shutdown check
        final int requestId = getNextRequestId();
        requestPacket.setRequestId(requestId);

        final ChannelWriteFailListenableFuture<ResponseMessage> future = new ChannelWriteFailListenableFuture<ResponseMessage>(timeoutMillis);

        final DefaultFuture old = this.requestMap.put(requestId, future);
        if (old != null) {
            throw new ELAgentSocketException("unexpected error. old future exist:" + old + " id:" + requestId);
        }

        // when future fails, put a handle in order to remove a failed future in the requestMap.
        FailureEventHandler removeTable = createFailureEventHandler(requestId);
        future.setFailureEventHandler(removeTable);

        addTimeoutTask(timeoutMillis, future);
        return future;
    }


    public void close() {
        logger.debug("close()");
        final ELAgentSocketException closed = new ELAgentSocketException("socket closed");

        // Could you handle race conditions of "close" more precisely?
//        final Timer timer = this.timer;
//        if (timer != null) {
//            Set<Timeout> stop = timer.stop();
//            for (Timeout timeout : stop) {
//                DefaultFuture future = (DefaultFuture)timeout.getTask();
//                future.setFailure(closed);
//            }
//        }
        int requestFailCount = 0;
        for (Map.Entry<Integer, DefaultFuture<ResponseMessage>> entry : requestMap.entrySet()) {
            if(entry.getValue().setFailure(closed)) {
                requestFailCount++;
            }
        }
        this.requestMap.clear();
        if (requestFailCount > 0) {
            logger.info("requestManager failCount:{}", requestFailCount);
        }

    }

}

