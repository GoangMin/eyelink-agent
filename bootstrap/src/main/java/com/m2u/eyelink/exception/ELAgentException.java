package com.m2u.eyelink.exception;

@SuppressWarnings("serial")
public class ELAgentException extends RuntimeException {
    public ELAgentException() {
    }

    public ELAgentException(String message) {
        super(message);
    }

    public ELAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ELAgentException(Throwable cause) {
        super(cause);
    }
}
