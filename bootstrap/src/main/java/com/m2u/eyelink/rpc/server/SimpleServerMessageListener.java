package com.m2u.eyelink.rpc.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.client.SimpleMessageListener;
import com.m2u.eyelink.rpc.packet.HandshakeResponseCode;
import com.m2u.eyelink.rpc.packet.HandshakeResponseType;
import com.m2u.eyelink.rpc.packet.PingPacket;

public class SimpleServerMessageListener extends SimpleMessageListener implements ServerMessageListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final SimpleServerMessageListener SIMPLEX_INSTANCE = new SimpleServerMessageListener(HandshakeResponseType.Success.SIMPLEX_COMMUNICATION);
    public static final SimpleServerMessageListener DUPLEX_INSTANCE = new SimpleServerMessageListener(HandshakeResponseType.Success.DUPLEX_COMMUNICATION);

    public static final SimpleServerMessageListener SIMPLEX_ECHO_INSTANCE = new SimpleServerMessageListener(true, HandshakeResponseType.Success.SIMPLEX_COMMUNICATION);
    public static final SimpleServerMessageListener DUPLEX_ECHO_INSTANCE = new SimpleServerMessageListener(true, HandshakeResponseType.Success.DUPLEX_COMMUNICATION);

    private final HandshakeResponseCode handshakeResponseCode;

    public SimpleServerMessageListener(HandshakeResponseCode handshakeResponseCode) {
        this(false, handshakeResponseCode);
    }

    public SimpleServerMessageListener(boolean echo, HandshakeResponseCode handshakeResponseCode) {
        super(echo);
        this.handshakeResponseCode = handshakeResponseCode;
    }

    @Override
    public HandshakeResponseCode handleHandshake(Map properties) {
        logger.info("handleHandshake properties:{}", properties);
        return handshakeResponseCode;
    }

    @Override
    public void handlePing(PingPacket pingPacket, ELAgentServer pinpointServer) {
        logger.info("handlePing packet:{}, remote:{} ", pingPacket, pinpointServer.getRemoteAddress());
    }

}
