package com.m2u.eyelink.rpc.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ELAgentSocket;
import com.m2u.eyelink.rpc.SocketStateCode;
import com.m2u.eyelink.rpc.StateChangeEventListener;
import com.m2u.eyelink.rpc.common.SocketState;
import com.m2u.eyelink.rpc.common.SocketStateChangeResult;

public class ELAgentClientHandlerState {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DefaultELAgentClientHandler clientHandler;
    private final List<StateChangeEventListener> stateChangeEventListeners;

    private final SocketState state;
    
    public ELAgentClientHandlerState(DefaultELAgentClientHandler clientHandler, List<StateChangeEventListener> stateChangeEventListeners) {
        this.clientHandler = clientHandler;
        this.stateChangeEventListeners = stateChangeEventListeners;

        this.state = new SocketState();
    }

    SocketStateChangeResult toBeingConnect() {
        SocketStateCode nextState = SocketStateCode.BEING_CONNECT;
        return to(nextState);
    }
    
    SocketStateChangeResult toConnected() {
        SocketStateCode nextState = SocketStateCode.CONNECTED;
        return to(nextState);
    }

    SocketStateChangeResult toConnectFailed() {
        SocketStateCode nextState = SocketStateCode.CONNECT_FAILED;
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
        SocketStateCode nextState = SocketStateCode.BEING_CLOSE_BY_CLIENT;
        return to(nextState);
    }

    SocketStateChangeResult toBeingCloseByPeer() {
        SocketStateCode nextState = SocketStateCode.BEING_CLOSE_BY_SERVER;
        return to(nextState);
    }

    SocketStateChangeResult toClosed() {
        SocketStateCode nextState = SocketStateCode.CLOSED_BY_CLIENT;
        return to(nextState);
    }

    SocketStateChangeResult toClosedByPeer() {
        SocketStateCode nextState = SocketStateCode.CLOSED_BY_SERVER;
        return to(nextState);
    }

    SocketStateChangeResult toUnexpectedClosed() {
        SocketStateCode nextState = SocketStateCode.UNEXPECTED_CLOSE_BY_CLIENT;
        return to(nextState);
    }

    SocketStateChangeResult toUnexpectedClosedByPeer() {
        SocketStateCode nextState = SocketStateCode.UNEXPECTED_CLOSE_BY_SERVER;
        return to(nextState);
    }

    SocketStateChangeResult toErrorUnknown() {
        SocketStateCode nextState = SocketStateCode.ERROR_UNKNOWN;
        return to(nextState);
    }
    
    private SocketStateChangeResult to(SocketStateCode nextState) {
        String objectName = clientHandler.getObjectName();
        ELAgentSocket pinpointSocket = clientHandler.getELAgentClient();

        logger.debug("{} stateTo() started. to:{}", objectName, nextState);

        SocketStateChangeResult stateChangeResult = state.to(nextState);
        if (stateChangeResult.isChange()) {
            executeChangeEventHandler(pinpointSocket, nextState);
        }

        logger.info("{} stateTo() completed. {}", objectName, stateChangeResult);
        return stateChangeResult;
    }

    private void executeChangeEventHandler(ELAgentSocket pinpointSocket, SocketStateCode nextState) {
        for (StateChangeEventListener eachListener : this.stateChangeEventListeners) {
            try {
                eachListener.eventPerformed(pinpointSocket, nextState);
            } catch (Exception e) {
                eachListener.exceptionCaught(pinpointSocket, nextState, e);
            }
        }
    }

    boolean isBeforeConnected(SocketStateCode currentStateCode) {
        return SocketStateCode.isBeforeConnected(currentStateCode);
    }
    
    boolean isEnableCommunication() {
        return SocketStateCode.isRun(getCurrentStateCode());
    }
    
    boolean isEnableCommunication(SocketStateCode currentStateCode) {
        return SocketStateCode.isRun(currentStateCode);
    }

    boolean isEnableDuplexCommunication() {
        return SocketStateCode.isRunDuplex(getCurrentStateCode());
    }
    
    boolean isClosed() {
        return SocketStateCode.isClosed(getCurrentStateCode());
    }

    boolean isClosed(SocketStateCode currentStateCode) {
        return SocketStateCode.isClosed(currentStateCode);
    }
    
    boolean onClose(SocketStateCode currentStateCode) {
        return SocketStateCode.onClose(currentStateCode);
    }

    boolean isReconnect(SocketStateCode currentStateCode) {
        if (currentStateCode == SocketStateCode.BEING_CLOSE_BY_SERVER) {
            return true;
        }

        if (currentStateCode == SocketStateCode.CLOSED_BY_SERVER) {
            return true;
        }
        
        if (currentStateCode == SocketStateCode.UNEXPECTED_CLOSE_BY_SERVER) {
            return true;
        }
        
        return false;
    }

    SocketStateCode getCurrentStateCode() {
        return state.getCurrentState();
    }
    
    @Override
    public String toString() {
        return state.toString();
    }
    
}
