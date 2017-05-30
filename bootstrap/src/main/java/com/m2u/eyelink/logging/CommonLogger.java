package com.m2u.eyelink.logging;

public interface CommonLogger {
    boolean isTraceEnabled();

    void trace(String msg);

    boolean isDebugEnabled();

    void debug(String msg);

    boolean isInfoEnabled();

    void info(String msg);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String msg, Throwable throwable);
}
