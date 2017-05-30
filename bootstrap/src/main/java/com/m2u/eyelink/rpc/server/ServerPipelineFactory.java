package com.m2u.eyelink.rpc.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.m2u.eyelink.rpc.client.PacketDecoder;
import com.m2u.eyelink.rpc.codec.PacketEncoder;
import com.m2u.eyelink.rpc.server.ELAgentServerAcceptor.ELAgentServerChannelHandler;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    private ELAgentServerChannelHandler pinpointServerChannelHandler;

    public ServerPipelineFactory(ELAgentServerChannelHandler pinpointServerChannelHandler) {
        if (pinpointServerChannelHandler == null) {
            throw new NullPointerException("PinpointServerFactory");
        }
        this.pinpointServerChannelHandler = pinpointServerChannelHandler;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new PacketDecoder());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", pinpointServerChannelHandler);

        return pipeline;
    }
}
