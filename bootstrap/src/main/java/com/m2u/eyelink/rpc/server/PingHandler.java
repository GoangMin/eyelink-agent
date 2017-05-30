package com.m2u.eyelink.rpc.server;

import com.m2u.eyelink.rpc.packet.PingPacket;

public interface PingHandler {
	void handlePing(PingPacket pingPacket, ELAgentServer pinpointServer); 
}
