package com.m2u.eyelink.rpc;

import java.util.concurrent.atomic.AtomicReference;

import com.m2u.eyelink.rpc.stream.ClientStreamChannel;
import com.m2u.eyelink.sender.StreamChannelContext;
import com.m2u.eyelink.sender.StreamCreateFailPacket;
import com.m2u.eyelink.util.AssertUtils;

public class ClientStreamChannelContext extends StreamChannelContext {
    private final ClientStreamChannel clientStreamChannel;
    private final ClientStreamChannelMessageListener clientStreamChannelMessageListener;
    private final AtomicReference<StreamCreateFailPacket> createFailPacketReference;

    public ClientStreamChannelContext(ClientStreamChannel clientStreamChannel, ClientStreamChannelMessageListener clientStreamChannelMessageListener) {
        AssertUtils.assertNotNull(clientStreamChannel);
        AssertUtils.assertNotNull(clientStreamChannelMessageListener);

        this.clientStreamChannel = clientStreamChannel;
        this.clientStreamChannelMessageListener = clientStreamChannelMessageListener;
        this.createFailPacketReference = new AtomicReference<StreamCreateFailPacket>();
    }

    @Override
    public ClientStreamChannel getStreamChannel() {
        return clientStreamChannel;
    }

    public ClientStreamChannelMessageListener getClientStreamChannelMessageListener() {
        return clientStreamChannelMessageListener;
    }

    public StreamCreateFailPacket getCreateFailPacket() {
        return createFailPacketReference.get();
    }

    public boolean setCreateFailPacket(StreamCreateFailPacket createFailPacket) {
        return this.createFailPacketReference.compareAndSet(null, createFailPacket);
    }
}
