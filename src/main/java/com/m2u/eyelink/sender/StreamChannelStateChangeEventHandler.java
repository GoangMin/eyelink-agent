package com.m2u.eyelink.sender;

public interface StreamChannelStateChangeEventHandler <S extends StreamChannel>{
    void eventPerformed(S streamChannel, StreamChannelStateCode updatedStateCode) throws Exception;

    void exceptionCaught(S streamChannel, StreamChannelStateCode updatedStateCode, Throwable e);

}
