package com.m2u.eyelink.rpc;

public class ELAgentSocketException  extends RuntimeException {
    public ELAgentSocketException() {
    }

    public ELAgentSocketException(String message) {
        super(message);
    }

    public ELAgentSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public ELAgentSocketException(Throwable cause) {
        super(cause);
    }
}