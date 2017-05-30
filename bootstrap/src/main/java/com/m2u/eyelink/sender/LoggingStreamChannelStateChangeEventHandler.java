package com.m2u.eyelink.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.StreamChannelStateChangeEventHandler;
import com.m2u.eyelink.rpc.stream.StreamChannel;

public class LoggingStreamChannelStateChangeEventHandler implements StreamChannelStateChangeEventHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void eventPerformed(StreamChannel streamChannel, StreamChannelStateCode updatedStateCode) throws Exception {
        logger.info("eventPerformed streamChannel:{}, stateCode:{}", streamChannel, updatedStateCode);
    }

    @Override
    public void exceptionCaught(StreamChannel streamChannel, StreamChannelStateCode updatedStateCode, Throwable e) {
        logger.warn("exceptionCaught message:{}, streamChannel:{}, stateCode:{}", e.getMessage(), streamChannel, updatedStateCode, e);
    }
}
