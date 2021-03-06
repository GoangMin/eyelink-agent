package com.m2u.eyelink.rpc.codec;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.packet.Packet;

public class PacketEncoder extends OneToOneEncoder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof Packet)) {
            logger.error("invalid packet:{} channel:{}", msg, channel);
            return null;
        }
        Packet packet = (Packet) msg;
        return packet.toBuffer();
    }
}
