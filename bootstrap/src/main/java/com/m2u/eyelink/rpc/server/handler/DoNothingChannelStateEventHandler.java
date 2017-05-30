package com.m2u.eyelink.rpc.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.server.ServerStateChangeEventHandler;

public class DoNothingChannelStateEventHandler implements ServerStateChangeEventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final ServerStateChangeEventHandler INSTANCE = new DoNothingChannelStateEventHandler();

    @Override
    public void eventPerformed(ELAgentServer pinpointServer, SocketStateCode stateCode) {
        logger.info("{} eventPerformed(). pinpointServer:{}, code:{}", this.getClass().getSimpleName(), pinpointServer, stateCode);
    }
    
    @Override
    public void exceptionCaught(ELAgentServer pinpointServer, SocketStateCode stateCode, Throwable e) {
        logger.warn("{} exceptionCaught(). pinpointServer:{}, code:{}. Error: {}.", this.getClass().getSimpleName(), pinpointServer, stateCode, e.getMessage(), e);
    }

}
