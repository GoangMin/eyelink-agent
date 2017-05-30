package com.m2u.eyelink.rpc.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectFuture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicReferenceFieldUpdater<ConnectFuture, Result> FIELD_UPDATER = AtomicReferenceFieldUpdater.newUpdater(ConnectFuture.class, Result.class, "result");

    private final CountDownLatch latch;
    private volatile Result result;

    public enum Result {
        SUCCESS, FAIL
    }

    public ConnectFuture() {
        this.latch = new CountDownLatch(1);
    }

    public Result getResult() {
        return this.result;
    }
    
    void setResult(Result connectResult) {
        final Result result = this.result;
        if (result == null) {
            if (FIELD_UPDATER.compareAndSet(this, null, connectResult)) {
                latch.countDown();
            }
        }
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return latch.await(timeout, timeUnit);
    }

    public void awaitUninterruptibly() {
        while (true) {
            try {
                await();
                return;
            } catch (InterruptedException e) {
                logger.debug(e.getMessage(), e);
            }
        }
    }

}
