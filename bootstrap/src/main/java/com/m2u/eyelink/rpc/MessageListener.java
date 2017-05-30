package com.m2u.eyelink.rpc;

import com.m2u.eyelink.rpc.packet.SendPacket;

public interface MessageListener {

    void handleSend(SendPacket sendPacket, ELAgentSocket pinpointSocket);

    void handleRequest(RequestPacket requestPacket, ELAgentSocket pinpointSocket);

}
