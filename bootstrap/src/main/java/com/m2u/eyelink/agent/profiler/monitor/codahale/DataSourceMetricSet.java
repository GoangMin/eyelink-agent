package com.m2u.eyelink.agent.profiler.monitor.codahale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.m2u.eyelink.context.monitor.DataSourceMonitorWrapper;
import com.m2u.eyelink.context.monitor.PluginMonitorWrapperLocator;


public class DataSourceMetricSet implements MetricSet {

    private final PluginMonitorWrapperLocator<DataSourceMonitorWrapper> dataSourceMonitorLocator;

    public DataSourceMetricSet(PluginMonitorWrapperLocator<DataSourceMonitorWrapper> dataSourceMonitorLocator) {
        this.dataSourceMonitorLocator = dataSourceMonitorLocator;
    }

    @Override
    public Map<String, Metric> getMetrics() {
        List<DataSourceMonitorWrapper> dataSourceMonitorList = dataSourceMonitorLocator.getPluginMonitorWrapperList();

        final Map<String, Metric> gauges = new HashMap<String, Metric>();
        for (DataSourceMonitorWrapper dataSourceMonitor : dataSourceMonitorList) {
            gauges.put(MetricMonitorValues.DATASOURCE + "." + dataSourceMonitor.getId(), new DataSourceGauge(dataSourceMonitor));
        }

        return gauges;
    }

}
