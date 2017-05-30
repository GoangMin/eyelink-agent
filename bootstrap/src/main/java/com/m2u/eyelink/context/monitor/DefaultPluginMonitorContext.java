package com.m2u.eyelink.context.monitor;

import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;
import com.m2u.eyelink.plugin.monitor.PluginMonitorContext;
import com.m2u.eyelink.plugin.monitor.PluginMonitorRegistry;

public class DefaultPluginMonitorContext implements PluginMonitorContext {

    private final DataSourceMonitorList dataSourceMonitorList;

    // it will be changed using ProfilerConfig
    public DefaultPluginMonitorContext() {
        this(5);
    }

    public DefaultPluginMonitorContext(int limitIdNumber) {
        dataSourceMonitorList = new DataSourceMonitorList(limitIdNumber);
    }

    @Override
    public PluginMonitorRegistry<DataSourceMonitor> getDataSourceMonitorRegistry() {
        return dataSourceMonitorList;
    }

    public PluginMonitorWrapperLocator<DataSourceMonitorWrapper> getDataSourceMonitorLocator() {
        return dataSourceMonitorList;
    }

}
