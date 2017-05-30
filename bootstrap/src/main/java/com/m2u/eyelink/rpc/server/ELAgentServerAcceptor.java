package com.m2u.eyelink.rpc.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerBossPool;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioWorkerPool;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import com.m2u.eyelink.rpc.ClusterOption;
import com.m2u.eyelink.rpc.DisabledServerStreamChannelMessageListener;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.ELAgentSocketException;
import com.m2u.eyelink.rpc.LoggerFactorySetup;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;
import com.m2u.eyelink.rpc.client.WriteFailFutureListener;
import com.m2u.eyelink.rpc.packet.PingPacket;
import com.m2u.eyelink.rpc.packet.ServerClosePacket;
import com.m2u.eyelink.rpc.util.CpuUtils;
import com.m2u.eyelink.rpc.util.TimerFactory;
import com.m2u.eyelink.util.AssertUtils;
import com.m2u.eyelink.util.ELAgentThreadFactory;

public class ELAgentServerAcceptor implements ELAgentServerConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long DEFAULT_TIMEOUTMILLIS = 3 * 1000;
    private static final int WORKER_COUNT = CpuUtils.workerCount();

    private volatile boolean released;

    private ServerBootstrap bootstrap;

    private InetAddress[] ignoreAddressList;

    private Channel serverChannel;
    private final ChannelGroup channelGroup = new DefaultChannelGroup("ELAgentServerFactory");

    private final ELAgentServerChannelHandler nettyChannelHandler = new ELAgentServerChannelHandler();

    private ServerMessageListener messageListener = SimpleServerMessageListener.SIMPLEX_INSTANCE;
    private ServerStreamChannelMessageListener serverStreamChannelMessageListener = DisabledServerStreamChannelMessageListener.INSTANCE;
    private List<ServerStateChangeEventHandler> stateChangeEventHandler = new ArrayList<ServerStateChangeEventHandler>();

    private final Timer healthCheckTimer;
    private final Timer requestManagerTimer;

    private final ClusterOption clusterOption;

    private long defaultRequestTimeout = DEFAULT_TIMEOUTMILLIS;

    static {
        LoggerFactorySetup.setupSlf4jLoggerFactory();
    }

    public ELAgentServerAcceptor() {
        this(ClusterOption.DISABLE_CLUSTER_OPTION);
    }

    public ELAgentServerAcceptor(ClusterOption clusterOption) {
        ServerBootstrap bootstrap = createBootStrap(1, WORKER_COUNT);
        setOptions(bootstrap);
        addPipeline(bootstrap);
        this.bootstrap = bootstrap;

        this.healthCheckTimer = TimerFactory.createHashedWheelTimer("ELAgentServerSocket-HealthCheckTimer", 50, TimeUnit.MILLISECONDS, 512);
        this.requestManagerTimer = TimerFactory.createHashedWheelTimer("ELAgentServerSocket-RequestManager", 50, TimeUnit.MILLISECONDS, 512);

        this.clusterOption = clusterOption;
    }

    private ServerBootstrap createBootStrap(int bossCount, int workerCount) {
        // profiler, collector
        ExecutorService boss = Executors.newCachedThreadPool(new ELAgentThreadFactory("ELAgent-Server-Boss"));
        NioServerBossPool nioServerBossPool = new NioServerBossPool(boss, bossCount, ThreadNameDeterminer.CURRENT);

        ExecutorService worker = Executors.newCachedThreadPool(new ELAgentThreadFactory("ELAgent-Server-Worker"));
        NioWorkerPool nioWorkerPool = new NioWorkerPool(worker, workerCount, ThreadNameDeterminer.CURRENT);

        NioServerSocketChannelFactory nioClientSocketChannelFactory = new NioServerSocketChannelFactory(nioServerBossPool, nioWorkerPool);
        return new ServerBootstrap(nioClientSocketChannelFactory);
    }

    private void setOptions(ServerBootstrap bootstrap) {
        // is read/write timeout necessary? don't need it because of NIO?
        // write timeout should be set through additional interceptor. write
        // timeout exists.

        // tcp setting
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        // buffer setting
        bootstrap.setOption("child.sendBufferSize", 1024 * 64);
        bootstrap.setOption("child.receiveBufferSize", 1024 * 64);

        // bootstrap.setOption("child.soLinger", 0);
    }

    private void addPipeline(ServerBootstrap bootstrap) {
        ServerPipelineFactory serverPipelineFactory = new ServerPipelineFactory(nettyChannelHandler);
        bootstrap.setPipelineFactory(serverPipelineFactory);
    }
    
    void setPipelineFactory(ChannelPipelineFactory channelPipelineFactory) {
        if (channelPipelineFactory == null) {
            throw new NullPointerException("channelPipelineFactory must not be null");
        }
        bootstrap.setPipelineFactory(channelPipelineFactory);
    }

    public void bind(String host, int port) throws ELAgentSocketException {
        InetSocketAddress bindAddress = new InetSocketAddress(host, port);
        bind(bindAddress);
    }

    public void bind(InetSocketAddress bindAddress) throws ELAgentSocketException {
        if (released) {
            return;
        }

        logger.info("bind() {}", bindAddress);
        this.serverChannel = bootstrap.bind(bindAddress);
        sendPing();
    }

    private DefaultELAgentServer createELAgentServer(Channel channel) {
        DefaultELAgentServer pinpointServer = new DefaultELAgentServer(channel, this);
        return pinpointServer;
    }

    @Override
    public long getDefaultRequestTimeout() {
        return defaultRequestTimeout;
    }

    public void setDefaultRequestTimeout(long defaultRequestTimeout) {
        this.defaultRequestTimeout = defaultRequestTimeout;
    }

    private boolean isIgnoreAddress(Channel channel) {
        if (ignoreAddressList == null) {
            return false;
        }
        final InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        if (remoteAddress == null) {
            return false;
        }
        InetAddress address = remoteAddress.getAddress();
        for (InetAddress ignore : ignoreAddressList) {
            if (ignore.equals(address)) {
                return true;
            }
        }
        return false;
    }

    public void setIgnoreAddressList(InetAddress[] ignoreAddressList) {
        AssertUtils.assertNotNull(ignoreAddressList, "ignoreAddressList must not be null");

        this.ignoreAddressList = ignoreAddressList;
    }

    @Override
    public ServerMessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(ServerMessageListener messageListener) {
        AssertUtils.assertNotNull(messageListener, "messageListener must not be null");

        this.messageListener = messageListener;
    }

    @Override
    public List<ServerStateChangeEventHandler> getStateChangeEventHandlers() {
        return stateChangeEventHandler;
    }

    public void addStateChangeEventHandler(ServerStateChangeEventHandler stateChangeEventHandler) {
        AssertUtils.assertNotNull(stateChangeEventHandler, "stateChangeEventHandler must not be null");

        this.stateChangeEventHandler.add(stateChangeEventHandler);
    }

    @Override
    public ServerStreamChannelMessageListener getStreamMessageListener() {
        return serverStreamChannelMessageListener;
    }

    public void setServerStreamChannelMessageListener(ServerStreamChannelMessageListener serverStreamChannelMessageListener) {
        AssertUtils.assertNotNull(serverStreamChannelMessageListener, "serverStreamChannelMessageListener must not be null");

        this.serverStreamChannelMessageListener = serverStreamChannelMessageListener;
    }

    @Override
    public Timer getHealthCheckTimer() {
        return healthCheckTimer;
    }

    @Override
    public Timer getRequestManagerTimer() {
        return requestManagerTimer;
    }

    @Override
    public ClusterOption getClusterOption() {
        return clusterOption;
    }

    private void sendPing() {
        logger.debug("sendPing");
        final TimerTask pintTask = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (timeout.isCancelled()) {
                    newPingTimeout(this);
                    return;
                }

                final ChannelGroupFuture write = channelGroup.write(PingPacket.PING_PACKET);
                if (logger.isWarnEnabled()) {
                    write.addListener(new ChannelGroupFutureListener() {
                        private final ChannelFutureListener listener = new WriteFailFutureListener(logger, "ping write fail", "ping write success");

                        @Override
                        public void operationComplete(ChannelGroupFuture future) throws Exception {

                            if (logger.isWarnEnabled()) {
                                for (ChannelFuture channelFuture : future) {
                                    channelFuture.addListener(listener);
                                }
                            }
                        }
                    });
                }
                newPingTimeout(this);
            }
        };
        newPingTimeout(pintTask);
    }

    private void newPingTimeout(TimerTask pintTask) {
        try {
            logger.debug("newPingTimeout");
            healthCheckTimer.newTimeout(pintTask, 1000 * 60 * 5, TimeUnit.MILLISECONDS);
        } catch (IllegalStateException e) {
            // stop in case of timer stopped
            logger.debug("timer stopped. Caused:{}", e.getMessage());
        }
    }
    
    public void close() {
        synchronized (this) {
            if (released) {
                return;
            }
            released = true;
        }
        healthCheckTimer.stop();
        
        closeELAgentServer();

        if (serverChannel != null) {
            ChannelFuture close = serverChannel.close();
            close.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);
            serverChannel = null;
        }
        if (bootstrap != null) {
            bootstrap.releaseExternalResources();
            bootstrap = null;
        }

        // clear the request first and remove timer
        requestManagerTimer.stop();
    }
    
    private void closeELAgentServer() {
        for (Channel channel : channelGroup) {
            DefaultELAgentServer pinpointServer = (DefaultELAgentServer) channel.getAttachment();

            if (pinpointServer != null) {
                pinpointServer.sendClosePacket();
            }
        }
    }
    
    public List<ELAgentSocket> getWritableSocketList() {
        List<ELAgentSocket> pinpointServerList = new ArrayList<ELAgentSocket>();

        for (Channel channel : channelGroup) {
            DefaultELAgentServer pinpointServer = (DefaultELAgentServer) channel.getAttachment();
            if (pinpointServer != null && pinpointServer.isEnableDuplexCommunication()) {
                pinpointServerList.add(pinpointServer);
            }
        }

        return pinpointServerList;
    }

    class ELAgentServerChannelHandler extends SimpleChannelHandler {
        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            final Channel channel = e.getChannel();
            logger.info("channelConnected channel:{}", channel);

            if (released) {
                logger.warn("already released. channel:{}", channel);
                channel.write(new ServerClosePacket()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        future.getChannel().close();
                    }
                });
                return;
            }

            boolean isIgnore = isIgnoreAddress(channel);
            if (isIgnore) {
                logger.debug("channelConnected ignore address. channel:" + channel);
                return;
            }

            DefaultELAgentServer pinpointServer = createELAgentServer(channel);
            
            channel.setAttachment(pinpointServer);
            channelGroup.add(channel);

            pinpointServer.start();

            super.channelConnected(ctx, e);
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            final Channel channel = e.getChannel();

            DefaultELAgentServer pinpointServer = (DefaultELAgentServer) channel.getAttachment();
            if (pinpointServer != null) {
                pinpointServer.stop(released);
            }

            super.channelDisconnected(ctx, e);
        }

        // ChannelClose event may also happen when the other party close socket
        // first and Disconnected occurs
        // Should consider that.
        @Override
        public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            final Channel channel = e.getChannel();

            channelGroup.remove(channel);

            super.channelClosed(ctx, e);
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            final Channel channel = e.getChannel();

            DefaultELAgentServer pinpointServer = (DefaultELAgentServer) channel.getAttachment();
            if (pinpointServer != null) {
                Object message = e.getMessage();

                pinpointServer.messageReceived(message);
            }

            super.messageReceived(ctx, e);
        }
    }

}
