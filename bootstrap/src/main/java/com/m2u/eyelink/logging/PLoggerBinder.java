package com.m2u.eyelink.logging;

public interface PLoggerBinder {
    PLogger getLogger(String name);

    void shutdown();
}
