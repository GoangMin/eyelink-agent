package com.m2u.eyelink.plugin.monitor;

public interface PluginMonitorRegistry<T> {
    boolean register(T pluginMonitor);

    boolean unregister(T pluginMonitor);
}
