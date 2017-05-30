package com.m2u.eyelink.context.monitor;

import java.util.List;

public interface PluginMonitorWrapperLocator<T extends PluginMonitorWrapper> {

    List<T> getPluginMonitorWrapperList();

}