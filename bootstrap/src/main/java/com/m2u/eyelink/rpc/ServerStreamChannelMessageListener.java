package com.m2u.eyelink.rpc;

import com.m2u.eyelink.sender.ServerStreamChannelContext;
import com.m2u.eyelink.sender.StreamClosePacket;
import com.m2u.eyelink.sender.StreamCode;
import com.m2u.eyelink.sender.StreamCreatePacket;

public interface ServerStreamChannelMessageListener {

    StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet);

    void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet);

}