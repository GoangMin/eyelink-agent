package com.m2u.eyelink.agent.profiler.plugin;

public interface ClassNameFilter {
    boolean ACCEPT = true;
    boolean REJECT = false;

    boolean accept(String className);
}