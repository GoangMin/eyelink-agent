package com.m2u.eyelink.sender;

import com.m2u.eyelink.rpc.Future;


public interface FutureListener<T> {
    void onComplete(Future<T> future);
}
