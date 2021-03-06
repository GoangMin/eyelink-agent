package com.m2u.eyelink.agent.profiler.monitor;

public class MonitorName {

    private final String name;

    public MonitorName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "MonitorName(" + this.name + ")";
    }

}
