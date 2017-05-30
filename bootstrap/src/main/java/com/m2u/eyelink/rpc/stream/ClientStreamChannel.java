package com.m2u.eyelink.rpc.stream;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.m2u.eyelink.sender.StreamChannelStateCode;
import com.m2u.eyelink.sender.StreamCreatePacket;

public class ClientStreamChannel extends StreamChannel {

    public ClientStreamChannel(Channel channel, int streamId, StreamChannelManager streamChannelManager) {
        super(channel, streamId, streamChannelManager);
    }

    public ChannelFuture sendCreate(byte[] payload) {
        assertState(StreamChannelStateCode.CONNECT_AWAIT);

        StreamCreatePacket packet = new StreamCreatePacket(getStreamId(), payload);
        return this.getChannel().write(packet);
    }

    boolean changeStateConnectAwait() {
        return changeStateTo(StreamChannelStateCode.CONNECT_AWAIT);
    }
}
