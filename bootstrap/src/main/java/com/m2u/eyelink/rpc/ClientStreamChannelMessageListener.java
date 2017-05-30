package com.m2u.eyelink.rpc;

import com.m2u.eyelink.sender.StreamClosePacket;
import com.m2u.eyelink.sender.StreamResponsePacket;

public interface ClientStreamChannelMessageListener {
	   void handleStreamData(ClientStreamChannelContext streamChannelContext, StreamResponsePacket packet);

	    void handleStreamClose(ClientStreamChannelContext streamChannelContext, StreamClosePacket packet);

}
