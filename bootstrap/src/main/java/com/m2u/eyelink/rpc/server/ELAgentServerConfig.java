package com.m2u.eyelink.rpc.server;

import java.util.List;

import org.jboss.netty.util.Timer;

import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;

public interface ELAgentServerConfig {

    long getDefaultRequestTimeout();
    
    Timer getHealthCheckTimer();
    Timer getRequestManagerTimer();

    ServerMessageListener getMessageListener();
    List<ServerStateChangeEventHandler> getStateChangeEventHandlers();

    ServerStreamChannelMessageListener getStreamMessageListener();

    ClusterOption getClusterOption();

}
