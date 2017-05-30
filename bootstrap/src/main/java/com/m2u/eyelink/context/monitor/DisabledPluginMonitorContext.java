package com.m2u.eyelink.context.monitor;

import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;
import com.m2u.eyelink.plugin.monitor.PluginMonitorContext;
import com.m2u.eyelink.plugin.monitor.PluginMonitorRegistry;

public class DisabledPluginMonitorContext implements PluginMonitorContext {

    private static DisabledPluginMonitorRegistry<DataSourceMonitor> DISABLED_DATASOURCE_REGISTRY = new DisabledPluginMonitorRegistry<DataSourceMonitor>();

    @Override
    public PluginMonitorRegistry<DataSourceMonitor> getDataSourceMonitorRegistry() {
        return DISABLED_DATASOURCE_REGISTRY;
    }

    private static class DisabledPluginMonitorRegistry<T> implements PluginMonitorRegistry<T> {

        @Override
        public boolean register(T pluginMonitor) {
            return false;
        }

        @Override
        public boolean unregister(T pluginMonitor) {
            return false;
        }

    }

}
