package com.m2u.eyelink.agent.profiler.sender;

import java.util.Collection;

public interface AsyncQueueingExecutorListener<T> {
    void execute(Collection<T> messageList);

    void execute(T message);
}
