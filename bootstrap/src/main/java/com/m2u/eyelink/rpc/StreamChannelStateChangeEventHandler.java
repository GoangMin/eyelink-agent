package com.m2u.eyelink.rpc;

import com.m2u.eyelink.rpc.stream.StreamChannel;
import com.m2u.eyelink.sender.StreamChannelStateCode;

public interface StreamChannelStateChangeEventHandler <S extends StreamChannel>{
    void eventPerformed(S streamChannel, StreamChannelStateCode updatedStateCode) throws Exception;

    void exceptionCaught(S streamChannel, StreamChannelStateCode updatedStateCode, Throwable e);

}
