package com.m2u.eyelink.rpc.server;

import java.util.Map;

import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.SocketStateCode;

public interface ELAgentServer extends ELAgentSocket {

    void messageReceived(Object message);

    SocketStateCode getCurrentStateCode();

    Map<Object, Object> getChannelProperties();
    
}
