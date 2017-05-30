package com.m2u.eyelink.rpc.server;

import com.m2u.eyelink.rpc.SocketStateCode;

public interface ServerStateChangeEventHandler {
    void eventPerformed(ELAgentServer pinpointServer, SocketStateCode stateCode) throws Exception;
    
    void exceptionCaught(ELAgentServer pinpointServer, SocketStateCode stateCode, Throwable e);

}
