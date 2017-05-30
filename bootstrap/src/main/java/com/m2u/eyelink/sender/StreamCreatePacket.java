package com.m2u.eyelink.sender;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.m2u.eyelink.rpc.packet.PacketType;
import com.m2u.eyelink.rpc.packet.PayloadPacket;
import com.m2u.eyelink.util.AssertUtils;

public class StreamCreatePacket extends BasicStreamPacket {

    private final static short PACKET_TYPE = PacketType.APPLICATION_STREAM_CREATE;

    private final byte[] payload;

    public StreamCreatePacket(int streamChannelId, byte[] payload) {
        super(streamChannelId);

        AssertUtils.assertNotNull(payload);
        this.payload = payload;
    }

    @Override
    public short getPacketType() {
        return PACKET_TYPE;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public ChannelBuffer toBuffer() {
        ChannelBuffer header = ChannelBuffers.buffer(2 + 4 + 4);
        header.writeShort(getPacketType());
        header.writeInt(getStreamChannelId());

        return PayloadPacket.appendPayload(header, payload);
    }

    public static StreamCreatePacket readBuffer(short packetType, ChannelBuffer buffer) {
        assert packetType == PACKET_TYPE;

        if (buffer.readableBytes() < 8) {
            buffer.resetReaderIndex();
            return null;
        }

        final int streamChannelId = buffer.readInt();
        final ChannelBuffer payload = PayloadPacket.readPayload(buffer);
        if (payload == null) {
            return null;
        }

        final StreamCreatePacket packet = new StreamCreatePacket(streamChannelId, payload.array());
        return packet;
    }

}
