package com.m2u.eyelink.agent.profiler.receiver;

import java.util.Set;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.context.thrift.SerializationUtils;
import com.m2u.eyelink.context.thrift.TResult;
import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.MessageListener;
import com.m2u.eyelink.rpc.RequestPacket;
import com.m2u.eyelink.rpc.ServerStreamChannelMessageListener;
import com.m2u.eyelink.rpc.packet.SendPacket;
import com.m2u.eyelink.sender.ServerStreamChannelContext;
import com.m2u.eyelink.sender.StreamClosePacket;
import com.m2u.eyelink.sender.StreamCode;
import com.m2u.eyelink.sender.StreamCreatePacket;

public class CommandDispatcher implements MessageListener, ServerStreamChannelMessageListener  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProfilerCommandServiceLocator commandServiceLocator;

    public CommandDispatcher(ProfilerCommandServiceLocator commandServiceLocator) {
        if (commandServiceLocator == null) {
            throw new NullPointerException("commandServiceLocator may not be null");
        }

        this.commandServiceLocator = commandServiceLocator;
    }

    @Override
    public void handleSend(SendPacket sendPacket, ELAgentSocket pinpointSocket) {
        logger.info("handleSend packet:{}, remote:{}", sendPacket, pinpointSocket.getRemoteAddress());
    }

    @Override
    public void handleRequest(RequestPacket requestPacket, ELAgentSocket pinpointSocket) {
        logger.info("handleRequest packet:{}, remote:{}", requestPacket, pinpointSocket.getRemoteAddress());

        final TBase<?, ?> request = SerializationUtils.deserialize(requestPacket.getPayload(), CommandSerializer.DESERIALIZER_FACTORY, null);
        logger.debug("handleRequest request:{}, remote:{}", request, pinpointSocket.getRemoteAddress());

        TBase response;
        if (request == null) {
            final TResult tResult = new TResult(false);
            tResult.setMessage("Unsupported ServiceTypeInfo.");

            response = tResult;
        } else {
            final ProfilerRequestCommandService service = commandServiceLocator.getRequestService(request);
            if (service == null) {
                TResult tResult = new TResult(false);
                tResult.setMessage("Can't find suitable service(" + request + ").");

                response = tResult;
            } else {
                response = service.requestCommandService(request);
            }
        }

        final byte[] payload = SerializationUtils.serialize(response, CommandSerializer.SERIALIZER_FACTORY, null);
        if (payload != null) {
            pinpointSocket.response(requestPacket, payload);
        }
    }

    @Override
    public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet) {
        logger.info("MessageReceived handleStreamCreate {} {}", packet, streamChannelContext);

        final TBase<?, ?> request = SerializationUtils.deserialize(packet.getPayload(), CommandSerializer.DESERIALIZER_FACTORY, null);
        if (request == null) {
            return StreamCode.TYPE_UNKNOWN;
        }
        
        final ProfilerStreamCommandService service = commandServiceLocator.getStreamService(request);
        if (service == null) {
            return StreamCode.TYPE_UNSUPPORT;
        }
        
        return service.streamCommandService(request, streamChannelContext);
    }

    @Override
    public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
    }

    public Set<Short> getRegisteredCommandServiceCodes() {
        return commandServiceLocator.getCommandServiceCodes();
    }

}
