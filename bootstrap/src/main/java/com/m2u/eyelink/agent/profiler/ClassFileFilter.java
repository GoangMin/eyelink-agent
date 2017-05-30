package com.m2u.eyelink.agent.profiler;

import java.security.ProtectionDomain;

public interface ClassFileFilter {
    boolean SKIP = false;
    boolean CONTINUE = true;

    boolean accept(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer);
}
