package com.m2u.eyelink.agent.profiler.plugin;

import java.util.HashSet;
import java.util.Set;

public class ClassLoadingChecker {

    private final Set<String> loadClass = new HashSet<String>();

    public boolean isFirstLoad(String className) {
        if (className == null) {
            throw new NullPointerException("className must not be null");
        }
        if (this.loadClass.add(className)) {
            // first load
            return true;
        }
        // already exist
        return false;
    }
}
