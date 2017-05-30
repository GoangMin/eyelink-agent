package com.m2u.eyelink.agent.profiler.monitor.codahale;

import java.util.Map;

import com.codahale.metrics.Metric;
import com.m2u.eyelink.context.thrift.TDataSource;
import com.m2u.eyelink.context.thrift.TDataSourceList;

public class DefaultDataSourceCollector implements DataSourceCollector {

    private final DataSourceMetricSet dataSourceMetricSet;

    public DefaultDataSourceCollector(DataSourceMetricSet dataSourceMetricSet) {
        this.dataSourceMetricSet = dataSourceMetricSet;
    }

    @Override
    public TDataSourceList collect() {
        TDataSourceList dataSourceList = new TDataSourceList();

        Map<String, Metric> metrics = dataSourceMetricSet.getMetrics();
        for (Metric metric : metrics.values()) {
            if (metric instanceof DataSourceGauge) {
                TDataSource dataSource = ((DataSourceGauge) metric).getValue();
                dataSourceList.addToDataSourceList(dataSource);
            }
        }

        return dataSourceList;
    }

}