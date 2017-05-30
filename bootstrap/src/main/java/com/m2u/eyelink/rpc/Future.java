package com.m2u.eyelink.rpc;

import com.m2u.eyelink.sender.FutureListener;


public interface Future<T> {

    T getResult();

    Throwable getCause();

    boolean isReady();

    boolean isSuccess();

    boolean setListener(FutureListener<T> listener);

    boolean await(long timeoutMillis);

    boolean await();
}
