package com.m2u.eyelink.rpc.stream;

import java.util.concurrent.atomic.AtomicReference;

import com.m2u.eyelink.sender.StreamChannelStateCode;

public class StreamChannelState {

    private final AtomicReference<StreamChannelStateCode> currentStateReference = new AtomicReference<StreamChannelStateCode>();

    public StreamChannelState() {
        currentStateReference.set(StreamChannelStateCode.NEW);
    }

    public StreamChannelStateCode getCurrentState() {
        return currentStateReference.get();
    }

    boolean to(StreamChannelStateCode nextState) {
        return to(currentStateReference.get(), nextState);
    }

    boolean to(StreamChannelStateCode currentState, StreamChannelStateCode nextState) {
        if (!nextState.canChangeState(currentState)) {
            return false;
        }

        boolean isChanged = currentStateReference.compareAndSet(currentState, nextState);
        return isChanged;
    }

    @Override
    public String toString() {
        return currentStateReference.get().name();
    }
}
