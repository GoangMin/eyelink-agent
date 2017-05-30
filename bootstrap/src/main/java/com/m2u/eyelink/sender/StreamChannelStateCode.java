package com.m2u.eyelink.sender;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public enum StreamChannelStateCode {
    NEW,
    OPEN(NEW),
    CONNECT_AWAIT(OPEN),
    CONNECT_ARRIVED(OPEN),
    CONNECTED(CONNECT_AWAIT, CONNECT_ARRIVED),
    CLOSED(CONNECT_AWAIT, CONNECT_ARRIVED, CONNECTED),
    ILLEGAL_STATE(NEW, OPEN, CONNECT_AWAIT, CONNECT_ARRIVED, CONNECTED, CLOSED);

    private final Set<StreamChannelStateCode> validBeforeStateSet;

    StreamChannelStateCode(StreamChannelStateCode... validBeforeStates) {
        this.validBeforeStateSet = new HashSet<StreamChannelStateCode>();

        if (validBeforeStates != null) {
            Collections.addAll(validBeforeStateSet, validBeforeStates);
        }
    }

    public boolean canChangeState(StreamChannelStateCode currentState) {
        if (validBeforeStateSet.contains(currentState)) {
            return true;
        }

        return false;
    }
}
