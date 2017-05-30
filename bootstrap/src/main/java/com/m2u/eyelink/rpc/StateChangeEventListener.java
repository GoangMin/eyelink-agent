package com.m2u.eyelink.rpc;

public interface StateChangeEventListener<S extends ELAgentSocket> {

    void eventPerformed(S pinpointSocket, SocketStateCode stateCode) throws Exception;

    void exceptionCaught(S pinpointSocket, SocketStateCode stateCode, Throwable e);

}
