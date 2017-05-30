package com.m2u.eyelink.rpc.stream;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.m2u.eyelink.sender.StreamChannelStateCode;
import com.m2u.eyelink.sender.StreamCreateSuccessPacket;
import com.m2u.eyelink.sender.StreamResponsePacket;

public class ServerStreamChannel extends StreamChannel {

    public ServerStreamChannel(Channel channel, int streamId, StreamChannelManager streamChannelManager) {
        super(channel, streamId, streamChannelManager);
    }

    public ChannelFuture sendData(byte[] payload) {
        assertState(StreamChannelStateCode.CONNECTED);

        StreamResponsePacket dataPacket = new StreamResponsePacket(getStreamId(), payload);
        return this.getChannel().write(dataPacket);
    }

    public ChannelFuture sendCreateSuccess() {
        assertState(StreamChannelStateCode.CONNECTED);

        StreamCreateSuccessPacket packet = new StreamCreateSuccessPacket(getStreamId());
        return this.getChannel().write(packet);
    }

    boolean changeStateConnectArrived() {
        return changeStateTo(StreamChannelStateCode.CONNECT_ARRIVED);
    }
}
