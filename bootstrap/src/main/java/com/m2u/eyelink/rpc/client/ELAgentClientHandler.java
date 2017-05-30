package com.m2u.eyelink.rpc.client;

import java.net.SocketAddress;

import com.m2u.eyelink.rpc.ClientStreamChannelContext;
import com.m2u.eyelink.rpc.ClientStreamChannelMessageListener;
import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.StreamChannelStateChangeEventHandler;
import com.m2u.eyelink.rpc.stream.ClientStreamChannel;
import com.m2u.eyelink.sender.StreamChannelContext;

public interface ELAgentClientHandler {
	   void setConnectSocketAddress(SocketAddress address);

	    void initReconnect();

	    ConnectFuture getConnectFuture();
	    
	    void setELAgentClient(ELAgentClient elagentClient);

	    void sendSync(byte[] bytes);

	    Future sendAsync(byte[] bytes);

	    void close();

	    void send(byte[] bytes);

	    Future<ResponseMessage> request(byte[] bytes);

	    void response(int requestId, byte[] payload);

	    ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener);
	    ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener);

	    StreamChannelContext findStreamChannel(int streamChannelId);
	    
	    void sendPing();

	    boolean isConnected();

	    boolean isSupportServerMode();
	    
	    SocketStateCode getCurrentStateCode();

	    SocketAddress getRemoteAddress();

	    ClusterOption getLocalClusterOption();
	    ClusterOption getRemoteClusterOption();

}
