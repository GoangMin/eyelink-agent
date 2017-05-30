package com.m2u.eyelink.rpc.client;

import org.jboss.netty.channel.Channel;

import com.m2u.eyelink.rpc.ClientStreamChannelContext;
import com.m2u.eyelink.rpc.ClientStreamChannelMessageListener;
import com.m2u.eyelink.rpc.StreamChannelStateChangeEventHandler;
import com.m2u.eyelink.rpc.stream.ClientStreamChannel;
import com.m2u.eyelink.rpc.stream.StreamChannelManager;
import com.m2u.eyelink.sender.StreamChannelContext;
import com.m2u.eyelink.sender.StreamPacket;

public class ELAgentClientHandlerContext {
    private final Channel channel;
    private final StreamChannelManager streamChannelManager;

    public ELAgentClientHandlerContext(Channel channel, StreamChannelManager streamChannelManager) {
        if (channel == null) {
            throw new NullPointerException("channel must not be null");
        }
        if (streamChannelManager == null) {
            throw new NullPointerException("streamChannelManager must not be null");
        }
        this.channel = channel;
        this.streamChannelManager = streamChannelManager;
    }

    public Channel getChannel() {
        return channel;
    }

    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
        return openStream(payload, messageListener, null);
    }

    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
        return streamChannelManager.openStream(payload, messageListener, stateChangeListener);
    }

    public void handleStreamEvent(StreamPacket message) {
        streamChannelManager.messageReceived(message);
    }

    public void closeAllStreamChannel() {
        streamChannelManager.close();
    }

    public StreamChannelContext getStreamChannel(int streamChannelId) {
        return streamChannelManager.findStreamChannel(streamChannelId);
    }
    
}
