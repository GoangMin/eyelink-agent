package com.m2u.eyelink.rpc.server;

import com.m2u.eyelink.rpc.MessageListener;

public interface ServerMessageListener extends MessageListener, HandshakerHandler, PingHandler {

}

