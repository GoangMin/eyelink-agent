package com.m2u.eyelink.sender;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.m2u.eyelink.rpc.packet.PacketType;

public class StreamClosePacket  extends BasicStreamPacket {

    private final static short PACKET_TYPE = PacketType.APPLICATION_STREAM_CLOSE;

    private final StreamCode code;

    public StreamClosePacket(int streamChannelId, short code) {
        this(streamChannelId, StreamCode.getCode(code));
    }

    public StreamClosePacket(int streamChannelId, StreamCode code) {
        super(streamChannelId);
        this.code = code;
    }

    @Override
    public short getPacketType() {
        return PACKET_TYPE;
    }

    @Override
    public ChannelBuffer toBuffer() {
        ChannelBuffer header = ChannelBuffers.buffer(2 + 4 + 2);
        header.writeShort(getPacketType());
        header.writeInt(getStreamChannelId());
        header.writeShort(code.value());

        return header;
    }

    public static StreamClosePacket readBuffer(short packetType, ChannelBuffer buffer) {
        assert packetType == PACKET_TYPE;

        if (buffer.readableBytes() < 6) {
            buffer.resetReaderIndex();
            return null;
        }

        final int streamChannelId = buffer.readInt();
        final short code = buffer.readShort();

        final StreamClosePacket packet = new StreamClosePacket(streamChannelId, code);
        return packet;
    }

    public StreamCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("{streamChannelId=").append(getStreamChannelId());
        sb.append(", ");
        sb.append("code=").append(getCode());
        sb.append('}');
        return sb.toString();
    }

}
