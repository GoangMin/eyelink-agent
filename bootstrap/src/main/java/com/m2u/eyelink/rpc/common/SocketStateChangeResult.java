package com.m2u.eyelink.rpc.common;

import com.m2u.eyelink.rpc.SocketStateCode;

public class SocketStateChangeResult {

    private final boolean isChange;
    
    private final SocketStateCode beforeState;
    private final SocketStateCode currentState;
    private final SocketStateCode updateWantedState;
    
    public SocketStateChangeResult(boolean isChange, SocketStateCode beforeState, SocketStateCode currentState, SocketStateCode updateWantedState) {
        this.isChange = isChange;
        this.beforeState = beforeState;
        this.currentState = currentState;
        this.updateWantedState = updateWantedState;
    }

    public boolean isChange() {
        return isChange;
    }

    public SocketStateCode getBeforeState() {
        return beforeState;
    }

    public SocketStateCode getCurrentState() {
        return currentState;
    }

    public SocketStateCode getUpdateWantedState() {
        return updateWantedState;
    }
    
    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append("Socket state change ");
        
        if (isChange) {
            toString.append("success");
        } else {
            toString.append("fail");
        }
        
        toString.append("(updateWanted:");
        toString.append(updateWantedState);

        toString.append(" ,before:");
        toString.append(beforeState);
        
        toString.append(" ,current:");
        toString.append(currentState);
        
        toString.append(").");
        
        return toString.toString();
    }

}