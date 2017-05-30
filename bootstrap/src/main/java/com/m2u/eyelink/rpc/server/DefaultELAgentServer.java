package com.m2u.eyelink.rpc.server;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ChannelWriteFailListenableFuture;
import com.m2u.eyelink.rpc.ClientStreamChannelContext;
import com.m2u.eyelink.rpc.ClientStreamChannelMessageListener;
import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.StreamChannelStateChangeEventHandler;
import com.m2u.eyelink.rpc.client.ControlMessageEncodingUtils;
import com.m2u.eyelink.rpc.client.RequestManager;
import com.m2u.eyelink.rpc.client.WriteFailFutureListener;
import com.m2u.eyelink.rpc.common.CyclicStateChecker;
import com.m2u.eyelink.rpc.common.SocketStateChangeResult;
import com.m2u.eyelink.rpc.control.ProtocolException;
import com.m2u.eyelink.rpc.packet.ControlHandshakePacket;
import com.m2u.eyelink.rpc.packet.ControlHandshakeResponsePacket;
import com.m2u.eyelink.rpc.packet.HandshakeResponseCode;
import com.m2u.eyelink.rpc.packet.Packet;
import com.m2u.eyelink.rpc.packet.PacketType;
import com.m2u.eyelink.rpc.packet.PingPacket;
import com.m2u.eyelink.rpc.packet.PongPacket;
import com.m2u.eyelink.rpc.packet.ResponsePacket;
import com.m2u.eyelink.rpc.packet.SendPacket;
import com.m2u.eyelink.rpc.packet.ServerClosePacket;
import com.m2u.eyelink.rpc.server.handler.DoNothingChannelStateEventHandler;
import com.m2u.eyelink.rpc.stream.ClientStreamChannel;
import com.m2u.eyelink.rpc.stream.StreamChannelManager;
import com.m2u.eyelink.rpc.util.ListUtils;
import com.m2u.eyelink.rpc.util.MapUtils;
import com.m2u.eyelink.sender.IDGenerator;
import com.m2u.eyelink.sender.Role;
import com.m2u.eyelink.sender.StreamChannelContext;
import com.m2u.eyelink.sender.StreamPacket;
import com.m2u.eyelink.util.AssertUtils;
import com.m2u.eyelink.util.ClassUtils;
import com.m2u.eyelink.util.StringUtils;

public class DefaultELAgentServer implements ELAgentServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Channel channel;
    private final RequestManager requestManager;

    private final DefaultELAgentServerState state;
    private final CyclicStateChecker stateChecker;

    private final ServerMessageListener messageListener;

    private final List<ServerStateChangeEventHandler> stateChangeEventListeners;

    private final StreamChannelManager streamChannelManager;

    private final AtomicReference<Map<Object, Object>> properties = new AtomicReference<Map<Object, Object>>();

    private final String objectUniqName;

    private final ClusterOption localClusterOption;
    private ClusterOption remoteClusterOption;

    private final ChannelFutureListener serverCloseWriteListener;
    private final ChannelFutureListener responseWriteFailListener;
    
    private final WriteFailFutureListener pongWriteFutureListener = new WriteFailFutureListener(logger, "pong write fail.", "pong write success.");
    
    
    public DefaultELAgentServer(Channel channel, ELAgentServerConfig serverConfig) {
        this(channel, serverConfig, null);
    }

    public DefaultELAgentServer(Channel channel, ELAgentServerConfig serverConfig, ServerStateChangeEventHandler... stateChangeEventListeners) {
        this.channel = channel;

        this.messageListener = serverConfig.getMessageListener();

        StreamChannelManager streamChannelManager = new StreamChannelManager(channel, IDGenerator.createEvenIdGenerator(), serverConfig.getStreamMessageListener());
        this.streamChannelManager = streamChannelManager;

        this.stateChangeEventListeners = new ArrayList<ServerStateChangeEventHandler>();
        List<ServerStateChangeEventHandler> configuredStateChangeEventHandlers = serverConfig.getStateChangeEventHandlers();
        if (configuredStateChangeEventHandlers != null) {
            for (ServerStateChangeEventHandler configuredStateChangeEventHandler : configuredStateChangeEventHandlers) {
                ListUtils.addIfValueNotNull(this.stateChangeEventListeners, configuredStateChangeEventHandler);
            }
        }
        ListUtils.addAllExceptNullValue(this.stateChangeEventListeners, stateChangeEventListeners);
        if (this.stateChangeEventListeners.isEmpty()) {
            this.stateChangeEventListeners.add(DoNothingChannelStateEventHandler.INSTANCE);
        }

        RequestManager requestManager = new RequestManager(serverConfig.getRequestManagerTimer(), serverConfig.getDefaultRequestTimeout());
        this.requestManager = requestManager;

        
        this.objectUniqName = ClassUtils.simpleClassNameAndHashCodeString(this);
        
        this.serverCloseWriteListener = new WriteFailFutureListener(logger, objectUniqName + " sendClosePacket() write fail.", "serverClosePacket write success");
        this.responseWriteFailListener = new WriteFailFutureListener(logger, objectUniqName + " response() write fail.");

        this.state = new DefaultELAgentServerState(this, this.stateChangeEventListeners);
        this.stateChecker = new CyclicStateChecker(5);

        this.localClusterOption = serverConfig.getClusterOption();
    }
    
    public void start() {
        logger.info("{} start() started. channel:{}.", objectUniqName, channel);
        
        state.toConnected();
        state.toRunWithoutHandshake();
        
        logger.info("{} start() completed.", objectUniqName);
    }
    
    public void stop() {
        logger.info("{} stop() started. channel:{}.", objectUniqName, channel);

        stop(false);
        
        logger.info("{} stop() completed.", objectUniqName);
    }
    
    public void stop(boolean serverStop) {
        try {
            SocketStateCode currentStateCode = getCurrentStateCode();
            if (SocketStateCode.BEING_CLOSE_BY_SERVER == currentStateCode) {
                state.toClosed();
            } else if (SocketStateCode.BEING_CLOSE_BY_CLIENT == currentStateCode) {
                state.toClosedByPeer();
            } else if (SocketStateCode.isRun(currentStateCode) && serverStop) {
                state.toUnexpectedClosed();
            } else if (SocketStateCode.isRun(currentStateCode)) {
                state.toUnexpectedClosedByPeer();
            } else if (SocketStateCode.isClosed(currentStateCode)) {
                logger.warn("{} stop(). Socket has closed state({}).", objectUniqName, currentStateCode);
            } else {
                state.toErrorUnknown();
                logger.warn("{} stop(). Socket has unexpected state.", objectUniqName, currentStateCode);
            }

            if (this.channel.isConnected()) {
                channel.close();
            }
        } finally {
            streamChannelManager.close();
        }
    }

    @Override
    public void send(byte[] payload) {
        AssertUtils.assertNotNull(payload, "payload may not be null.");
        if (!isEnableDuplexCommunication()) {
            throw new IllegalStateException("Send fail. Error: Illegal State. pinpointServer:" + toString());
        }
        
        SendPacket send = new SendPacket(payload);
        write0(send);
    }

    @Override
    public Future<ResponseMessage> request(byte[] payload) {
        AssertUtils.assertNotNull(payload, "payload may not be null.");
        if (!isEnableDuplexCommunication()) {
            throw new IllegalStateException("Request fail. Error: Illegal State. pinpointServer:" + toString());
        }

        RequestPacket requestPacket = new RequestPacket(payload);
        ChannelWriteFailListenableFuture<ResponseMessage> messageFuture = this.requestManager.register(requestPacket);
        write0(requestPacket, messageFuture);
        return messageFuture;
    }

    @Override
    public void response(RequestPacket requestPacket, byte[] payload) {
        response(requestPacket.getRequestId(), payload);
    }

    @Override
    public void response(int requestId, byte[] payload) {
        AssertUtils.assertNotNull(payload, "payload may not be null.");
        if (!isEnableCommunication()) {
            throw new IllegalStateException("Response fail. Error: Illegal State. pinpointServer:" + toString());
        }

        ResponsePacket responsePacket = new ResponsePacket(requestId, payload);
        write0(responsePacket, responseWriteFailListener);
    }
    
    private ChannelFuture write0(Object message) {
        return write0(message, null);
    }

    private ChannelFuture write0(Object message, ChannelFutureListener futureListener) {
        ChannelFuture future = channel.write(message);
        if (futureListener != null) {
            future.addListener(futureListener);
        }
        return future;
    }

    public StreamChannelContext getStreamChannel(int channelId) {
        return streamChannelManager.findStreamChannel(channelId);
    }

    @Override
    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
        return openStream(payload, messageListener, null);
    }

    @Override
    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
        logger.info("{} createStream() started.", objectUniqName);

        ClientStreamChannelContext streamChannel = streamChannelManager.openStream(payload, messageListener, stateChangeListener);

        logger.info("{} createStream() completed.", objectUniqName);
        return streamChannel;
    }

    public void closeAllStreamChannel() {
        logger.info("{} closeAllStreamChannel() started.", objectUniqName);

        streamChannelManager.close();

        logger.info("{} closeAllStreamChannel() completed.", objectUniqName);
    }
    
    @Override
    public Map<Object, Object> getChannelProperties() {
        Map<Object, Object> properties = this.properties.get();
        return properties == null ? Collections.emptyMap() : properties;
    }

    public boolean setChannelProperties(Map<Object, Object> value) {
        if (value == null) {
            return false;
        }

        return this.properties.compareAndSet(null, Collections.unmodifiableMap(value));
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    public ChannelFuture sendClosePacket() {
        logger.info("{} sendClosePacket() started.", objectUniqName);
        
        SocketStateChangeResult stateChangeResult = state.toBeingClose();
        if (stateChangeResult.isChange()) {
            final ChannelFuture writeFuture = this.channel.write(ServerClosePacket.DEFAULT_SERVER_CLOSE_PACKET);
            writeFuture.addListener(serverCloseWriteListener);

            logger.info("{} sendClosePacket() completed.", objectUniqName);
            return writeFuture;
        } else {
            logger.info("{} sendClosePacket() failed. Error:{}.", objectUniqName, stateChangeResult);
            return null;
        }
    }

    @Override
    public void messageReceived(Object message) {
        if (!isEnableCommunication()) {
            // FIXME need change rules.
            // as-is : do nothing when state is not run.
            // candidate : close channel when state is not run.
            logger.warn("{} messageReceived() failed. Error: Illegal state this message({}) will be ignore.", objectUniqName, message);
            return;
        }
        
        final short packetType = getPacketType(message);
        switch (packetType) {
            case PacketType.APPLICATION_SEND: {
                handleSend((SendPacket) message);
                return;
            }
            case PacketType.APPLICATION_REQUEST: {
                handleRequest((RequestPacket) message);
                return;
            }
            case PacketType.APPLICATION_RESPONSE: {
                handleResponse((ResponsePacket) message);
                return;
            }
            case PacketType.APPLICATION_STREAM_CREATE:
            case PacketType.APPLICATION_STREAM_CLOSE:
            case PacketType.APPLICATION_STREAM_CREATE_SUCCESS:
            case PacketType.APPLICATION_STREAM_CREATE_FAIL:
            case PacketType.APPLICATION_STREAM_RESPONSE:
            case PacketType.APPLICATION_STREAM_PING:
            case PacketType.APPLICATION_STREAM_PONG:
                handleStreamEvent((StreamPacket) message);
                return;
            case PacketType.CONTROL_HANDSHAKE:
                handleHandshake((ControlHandshakePacket) message);
                return;
            case PacketType.CONTROL_CLIENT_CLOSE: {
                handleClosePacket(channel);
                return;
            }
            case PacketType.CONTROL_PING: {
                handlePingPacket(channel, (PingPacket) message);
                return;
            }            
            default: {
                logger.warn("invalid messageReceived msg:{}, connection:{}", message, channel);
            }
        }
    }

    private short getPacketType(Object packet) {
        if (packet == null) {
            return PacketType.UNKNOWN;
        }

        if (packet instanceof Packet) {
            return ((Packet) packet).getPacketType();
        }

        return PacketType.UNKNOWN;
    }

    private void handleSend(SendPacket sendPacket) {
        messageListener.handleSend(sendPacket, this);
    }

    private void handleRequest(RequestPacket requestPacket) {
        messageListener.handleRequest(requestPacket, this);
    }

    private void handleResponse(ResponsePacket responsePacket) {
        this.requestManager.messageReceived(responsePacket, this);
    }

    private void handleStreamEvent(StreamPacket streamPacket) {
        streamChannelManager.messageReceived(streamPacket);
    }

    private void handleHandshake(ControlHandshakePacket handshakepacket) {
        logger.info("{} handleHandshake() started. Packet:{}", objectUniqName, handshakepacket);
        
        int requestId = handshakepacket.getRequestId();
        Map<Object, Object> handshakeData = decodeHandshakePacket(handshakepacket);
        HandshakeResponseCode responseCode = messageListener.handleHandshake(handshakeData);
        boolean isFirst = setChannelProperties(handshakeData);
        if (isFirst) {
            if (HandshakeResponseCode.DUPLEX_COMMUNICATION == responseCode) {
                this.remoteClusterOption = getClusterOption(handshakeData);
                state.toRunDuplex();
            } else if (HandshakeResponseCode.SIMPLEX_COMMUNICATION == responseCode || HandshakeResponseCode.SUCCESS == responseCode) {
                state.toRunSimplex();
            }
        }

        logger.info("{} handleHandshake(). ResponseCode:{}", objectUniqName, responseCode);

        Map<String, Object> responseData = createHandshakeResponse(responseCode, isFirst);
        sendHandshakeResponse0(requestId, responseData);
        
        logger.info("{} handleHandshake() completed.", objectUniqName);
    }

    private ClusterOption getClusterOption(Map handshakeResponse) {
        if (handshakeResponse == Collections.EMPTY_MAP) {
            return ClusterOption.DISABLE_CLUSTER_OPTION;
        }

        Map cluster = (Map) handshakeResponse.get(ControlHandshakeResponsePacket.CLUSTER);
        if (cluster == null) {
            return ClusterOption.DISABLE_CLUSTER_OPTION;
        }

        String id = MapUtils.getString(cluster, "id", "");
        List<Role> roles = getRoles((List) cluster.get("roles"));

        if (StringUtils.isEmpty(id)) {
            return ClusterOption.DISABLE_CLUSTER_OPTION;
        } else {
            return new ClusterOption(true, id, roles);
        }
    }

    private List<Role> getRoles(List roleNames) {
        List<Role> roles = new ArrayList<Role>();
        for (Object roleName : roleNames) {
            if (roleName instanceof String && !StringUtils.isEmpty((String) roleName)) {
                roles.add(Role.getValue((String) roleName));
            }
        }
        return roles;
    }

    private void handleClosePacket(Channel channel) {
        logger.info("{} handleClosePacket() started.", objectUniqName);
        
        SocketStateChangeResult stateChangeResult = state.toBeingCloseByPeer();
        if (!stateChangeResult.isChange()) {
            logger.info("{} handleClosePacket() failed. Error: {}", objectUniqName, stateChangeResult);
        } else {
            logger.info("{} handleClosePacket() completed.", objectUniqName);
        }
    }
    
    private void handlePingPacket(Channel channel, PingPacket packet) {
        logger.debug("{} handlePingPacket() started. packet:{}", objectUniqName, packet);
        
        SocketStateCode statusCode = state.getCurrentStateCode();

        if (statusCode.getId() == packet.getStateCode()) {
            stateChecker.unmark();
            
            messageListener.handlePing(packet, this);

            PongPacket pongPacket = PongPacket.PONG_PACKET;
            ChannelFuture write = channel.write(pongPacket);
            write.addListener(pongWriteFutureListener);
        } else {
            logger.warn("Session state sync failed. channel:{}, packet:{}, server-state:{}", channel, packet, statusCode);
            
            if (stateChecker.markAndCheckCondition()) {
                state.toErrorSyncStateSession();
                stop();
            }
        }
    }

    private Map<String, Object> createHandshakeResponse(HandshakeResponseCode responseCode, boolean isFirst) {
        HandshakeResponseCode createdCode = null;
        if (isFirst) {
            createdCode = responseCode;
        } else {
            if (HandshakeResponseCode.DUPLEX_COMMUNICATION == responseCode) {
                createdCode = HandshakeResponseCode.ALREADY_DUPLEX_COMMUNICATION;
            } else if (HandshakeResponseCode.SIMPLEX_COMMUNICATION == responseCode) {
                createdCode = HandshakeResponseCode.ALREADY_SIMPLEX_COMMUNICATION;
            } else {
                createdCode = responseCode;
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ControlHandshakeResponsePacket.CODE, createdCode.getCode());
        result.put(ControlHandshakeResponsePacket.SUB_CODE, createdCode.getSubCode());
        if (localClusterOption.isEnable()) {
            result.put(ControlHandshakeResponsePacket.CLUSTER, localClusterOption.getProperties());
        }

        return result;
    }

    private void sendHandshakeResponse0(int requestId, Map<String, Object> data) {
        try {
            byte[] resultPayload = ControlMessageEncodingUtils.encode(data);
            ControlHandshakeResponsePacket packet = new ControlHandshakeResponsePacket(requestId, resultPayload);

            channel.write(packet);
        } catch (ProtocolException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private Map<Object, Object> decodeHandshakePacket(ControlHandshakePacket message) {
        try {
            byte[] payload = message.getPayload();
            Map<Object, Object> properties = (Map) ControlMessageEncodingUtils.decode(payload);
            return properties;
        } catch (ProtocolException e) {
            logger.warn(e.getMessage(), e);
        }

        return Collections.EMPTY_MAP;
    }

    public boolean isEnableCommunication() {
        return state.isEnableCommunication();
    }
    
    public boolean isEnableDuplexCommunication() {
        return state.isEnableDuplexCommunication();
    }

    String getObjectUniqName() {
        return objectUniqName;
    }

    @Override
    public ClusterOption getLocalClusterOption() {
        return localClusterOption;
    }

    @Override
    public ClusterOption getRemoteClusterOption() {
        return remoteClusterOption;
    }

    @Override
    public SocketStateCode getCurrentStateCode() {
        return state.getCurrentStateCode();
    }

    @Override
    public void close() {
        stop();
    }

    @Override
    public String toString() {
        StringBuilder log = new StringBuilder(32);
        log.append(objectUniqName);
        log.append("(");
        log.append("remote:");
        log.append(getRemoteAddress());
        log.append(", state:");
        log.append(getCurrentStateCode());
        log.append(")");
        
        return log.toString();
    }
    
}
