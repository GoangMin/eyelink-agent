package com.m2u.eyelink.agent.profiler.sender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryQueue {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // Want to make message with less retry count higher priority.
    // But PriorityQueue of JDK has no size limit, so let's do it without priority for now.
    private final BlockingQueue<RetryMessage> queue;
    private final int capacity;
    private final int maxRetryCount;
    private final int halfCapacity;


    public RetryQueue(int capacity, int maxRetryCount) {
        this.queue = new LinkedBlockingQueue<RetryMessage>();
        this.capacity = capacity;
        this.halfCapacity = capacity / 2;
        this.maxRetryCount = maxRetryCount;
    }

    public RetryQueue() {
        this(1024, 3);
    }

    public void add(RetryMessage retryMessage) {
        if (retryMessage == null) {
            throw new NullPointerException("retryMessage must not be null");
        }

        if (!retryMessage.isRetryAvailable()) {
            logger.warn("discard retry message({}).", retryMessage);
            return;
        }
        int retryCount = retryMessage.getRetryCount();
        if (retryCount >= this.maxRetryCount) {
            logger.warn("discard retry message({}). queue-maxRetryCount:{}", retryMessage, maxRetryCount);
            return;
        }
        final int queueSize = queue.size();
        if (queueSize >= capacity) {
            logger.warn("discard retry message. queueSize:{}", queueSize);
            return;
        }
        if (queueSize >= halfCapacity && retryCount >= 1) {
            logger.warn("discard retry message. retryCount:{}", retryCount);
            return;
        }
        final boolean offer = this.queue.offer(retryMessage);
        if (!offer) {
            logger.warn("offer() fail. discard retry message. retryCount:{}", retryCount);
        }
    }

    public RetryMessage get() {
        return this.queue.poll();
    }

    public int size() {
        return this.queue.size();
    }
}
