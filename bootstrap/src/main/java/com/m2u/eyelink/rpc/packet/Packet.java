package com.m2u.eyelink.rpc.packet;

import org.jboss.netty.buffer.ChannelBuffer;

public interface Packet {
    short getPacketType();

    byte[] getPayload();

   ChannelBuffer toBuffer();
}
