package com.m2u.eyelink.rpc.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ServerClosePacket extends BasicPacket {

    public static final ServerClosePacket DEFAULT_SERVER_CLOSE_PACKET = new ServerClosePacket();
    private static final byte[] DEFAULT_SERVER_CLOSE_PACKET_BUFFER;
    static {
        ChannelBuffer buffer = ChannelBuffers.buffer(6);
        buffer.writeShort(PacketType.CONTROL_SERVER_CLOSE);
        buffer.writeInt(-1);
        DEFAULT_SERVER_CLOSE_PACKET_BUFFER = buffer.array();
    }
    
    @Override
    public short getPacketType() {
        return PacketType.CONTROL_SERVER_CLOSE;
    }

    @Override
    public ChannelBuffer toBuffer() {
        if (DEFAULT_SERVER_CLOSE_PACKET == this) {
            return ChannelBuffers.wrappedBuffer(DEFAULT_SERVER_CLOSE_PACKET_BUFFER);
        }
        
        ChannelBuffer header = ChannelBuffers.buffer(2 + 4);
        header.writeShort(PacketType.CONTROL_SERVER_CLOSE);

        return PayloadPacket.appendPayload(header, payload);
    }

    public static ServerClosePacket readBuffer(short packetType, ChannelBuffer buffer) {
        assert packetType == PacketType.CONTROL_SERVER_CLOSE;

        if (buffer.readableBytes() < 4) {
            buffer.resetReaderIndex();
            return null;
        }

        final ChannelBuffer payload = PayloadPacket.readPayload(buffer);
        if (payload == null) {
            return null;
        }
        final ServerClosePacket requestPacket = new ServerClosePacket();
        return requestPacket;

    }

    @Override
    public String toString() {
        return "ServerClosePacket";
    }
}
