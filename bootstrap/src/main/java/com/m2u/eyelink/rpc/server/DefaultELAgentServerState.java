package com.m2u.eyelink.rpc.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.common.SocketState;
import com.m2u.eyelink.rpc.common.SocketStateChangeResult;

public class DefaultELAgentServerState {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DefaultELAgentServer elagentServer;
    private final List<ServerStateChangeEventHandler> stateChangeEventListeners;

    private final SocketState state;

    public DefaultELAgentServerState(DefaultELAgentServer pinpointServer, List<ServerStateChangeEventHandler> stateChangeEventListeners) {
        this.elagentServer = pinpointServer;
        this.stateChangeEventListeners = stateChangeEventListeners;
        
        this.state = new SocketState();
    }

    SocketStateChangeResult toConnected() {
        SocketStateCode nextState = SocketStateCode.CONNECTED;
        return to(nextState);
    }

    SocketStateChangeResult toRunWithoutHandshake() {
        SocketStateCode nextState = SocketStateCode.RUN_WITHOUT_HANDSHAKE;
        return to(nextState);
    }

    SocketStateChangeResult toRunSimplex() {
        SocketStateCode nextState = SocketStateCode.RUN_SIMPLEX;
        return to(nextState);
    }

    SocketStateChangeResult toRunDuplex() {
        SocketStateCode nextState = SocketStateCode.RUN_DUPLEX;
        return to(nextState);
    }

    SocketStateChangeResult toBeingClose() {
        SocketStateCode nextState = SocketStateCode.BEING_CLOSE_BY_SERVER;
        return to(nextState);
    }

    SocketStateChangeResult toBeingCloseByPeer() {
        SocketStateCode nextState = SocketStateCode.BEING_CLOSE_BY_CLIENT;
        return to(nextState);
    }

    SocketStateChangeResult toClosed() {
        SocketStateCode nextState = SocketStateCode.CLOSED_BY_SERVER;
        return to(nextState);
    }

    SocketStateChangeResult toClosedByPeer() {
        SocketStateCode nextState = SocketStateCode.CLOSED_BY_CLIENT;
        return to(nextState);
    }

    SocketStateChangeResult toUnexpectedClosed() {
        SocketStateCode nextState = SocketStateCode.UNEXPECTED_CLOSE_BY_SERVER;
        return to(nextState);
    }

    SocketStateChangeResult toUnexpectedClosedByPeer() {
        SocketStateCode nextState = SocketStateCode.UNEXPECTED_CLOSE_BY_CLIENT;
        return to(nextState);
    }

    SocketStateChangeResult toErrorUnknown() {
        SocketStateCode nextState = SocketStateCode.ERROR_UNKNOWN;
        return to(nextState);
    }

    SocketStateChangeResult toErrorSyncStateSession() {
        SocketStateCode nextState = SocketStateCode.ERROR_SYNC_STATE_SESSION;
        return to(nextState);
    }

    private SocketStateChangeResult to(SocketStateCode nextState) {
        String objectUniqName = elagentServer.getObjectUniqName();
        
        logger.debug("{} stateTo() started. to:{}", objectUniqName, nextState);

        SocketStateChangeResult stateChangeResult = state.to(nextState);
        if (stateChangeResult.isChange()) {
            executeChangeEventHandler(elagentServer, nextState);
        }

        logger.info("{} stateTo() completed. {}", objectUniqName, stateChangeResult);

        return stateChangeResult;
    }

    private void executeChangeEventHandler(DefaultELAgentServer pinpointServer, SocketStateCode nextState) {
        for (ServerStateChangeEventHandler eachListener : this.stateChangeEventListeners) {
            try {
                eachListener.eventPerformed(pinpointServer, nextState);
            } catch (Exception e) {
                eachListener.exceptionCaught(pinpointServer, nextState, e);
            }
        }
    }

    boolean isEnableCommunication() {
        return SocketStateCode.isRun(getCurrentStateCode());
    }

    boolean isEnableDuplexCommunication() {
        return SocketStateCode.isRunDuplex(getCurrentStateCode());
    }
    
    SocketStateCode getCurrentStateCode() {
        return state.getCurrentState();
    }

}
