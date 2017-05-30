package com.m2u.eyelink.sender;

import org.apache.thrift.TBase;

public interface DataSender {
    boolean send(TBase<?, ?> data);

    void stop();
}
