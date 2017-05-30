package com.m2u.eyelink.context;

public interface TraceScope {
    String getName();

    boolean tryEnter();
    boolean canLeave();
    void leave();

    boolean isActive();
}
