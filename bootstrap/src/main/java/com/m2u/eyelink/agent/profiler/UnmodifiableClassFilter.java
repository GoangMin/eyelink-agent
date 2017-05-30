package com.m2u.eyelink.agent.profiler;

import java.security.ProtectionDomain;

public class UnmodifiableClassFilter implements ClassFileFilter {


    public UnmodifiableClassFilter() {
    }

    @Override
    public boolean accept(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) {
        // fast skip java classes
        if (className.startsWith("java")) {
            if (className.startsWith("/", 4) || className.startsWith("x/", 4)) {
                return SKIP;
            }
        }

        return CONTINUE;
    }
}
