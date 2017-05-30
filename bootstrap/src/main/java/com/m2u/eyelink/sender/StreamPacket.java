package com.m2u.eyelink.sender;

import com.m2u.eyelink.rpc.packet.Packet;


public interface StreamPacket extends Packet {

    int getStreamChannelId();

}
