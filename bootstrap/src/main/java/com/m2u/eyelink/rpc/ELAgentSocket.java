package com.m2u.eyelink.rpc;

import java.net.SocketAddress;

import com.m2u.eyelink.rpc.stream.ClientStreamChannel;

public interface ELAgentSocket {
    void send(byte[] payload);

    Future<ResponseMessage> request(byte[] payload);

    void response(RequestPacket requestPacket, byte[] payload);
    void response(int requestId, byte[] payload);

    ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener);
    ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener);

    SocketAddress getRemoteAddress();

    void close();

    ClusterOption getLocalClusterOption();
    ClusterOption getRemoteClusterOption();
}
