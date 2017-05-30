package com.m2u.eyelink.sender;

import com.m2u.eyelink.rpc.stream.ServerStreamChannel;

public class ServerStreamChannelContext extends StreamChannelContext{
    private ServerStreamChannel streamChannel;

    public ServerStreamChannelContext(ServerStreamChannel streamChannel) {
        this.streamChannel = streamChannel;
    }

    @Override
    public ServerStreamChannel getStreamChannel() {
        return streamChannel;
    }
}
