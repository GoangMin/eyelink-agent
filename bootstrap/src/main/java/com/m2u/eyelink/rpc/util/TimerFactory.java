package com.m2u.eyelink.rpc.util;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.ThreadNameDeterminer;

import com.m2u.eyelink.util.ELAgentThreadFactory;


public class TimerFactory {

    public static HashedWheelTimer createHashedWheelTimer(String threadName, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        final ELAgentThreadFactory threadFactory = new ELAgentThreadFactory(threadName, true);
        return createHashedWheelTimer(threadFactory, tickDuration, unit, ticksPerWheel);
    }

    public static HashedWheelTimer createHashedWheelTimer(ELAgentThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        return new HashedWheelTimer(threadFactory, ThreadNameDeterminer.CURRENT, tickDuration, unit, ticksPerWheel);
    }

}
