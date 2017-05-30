package com.m2u.eyelink.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.sender.ServerStreamChannelContext;
import com.m2u.eyelink.sender.StreamClosePacket;
import com.m2u.eyelink.sender.StreamCode;
import com.m2u.eyelink.sender.StreamCreatePacket;

public class DisabledServerStreamChannelMessageListener implements ServerStreamChannelMessageListener {
    public static final ServerStreamChannelMessageListener INSTANCE = new DisabledServerStreamChannelMessageListener();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet) {
        logger.info("{} handleStreamCreate unsupported operation. StreamChannel:{}, Packet:{}", this.getClass().getSimpleName(), streamChannelContext, packet);
        return StreamCode.CONNECTION_UNSUPPORT;
    }

    @Override
    public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
        logger.info("{} handleStreamClose unsupported operation. StreamChannel:{}, Packet:{}", this.getClass().getSimpleName(), streamChannelContext, packet);
    }

}
