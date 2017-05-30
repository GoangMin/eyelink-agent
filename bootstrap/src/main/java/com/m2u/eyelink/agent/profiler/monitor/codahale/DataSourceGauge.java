package com.m2u.eyelink.agent.profiler.monitor.codahale;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.m2u.eyelink.context.monitor.DataSourceMonitorWrapper;
import com.m2u.eyelink.context.thrift.TDataSource;

public class DataSourceGauge implements Gauge<TDataSource> {

    private final DataSourceMonitorWrapper dataSourceMonitorWrapper;

    protected DataSourceGauge(DataSourceMonitorWrapper dataSourceMonitorWrapper) {
        this.dataSourceMonitorWrapper = dataSourceMonitorWrapper;
    }

    @Override
    public TDataSource getValue() {
        TDataSource dataSource = new TDataSource();
        dataSource.setId(dataSourceMonitorWrapper.getId());
        dataSource.setServiceTypeCode(dataSourceMonitorWrapper.getServiceType().getCode());

        String name = dataSourceMonitorWrapper.getName();
        if (name != null) {
            dataSource.setName(name);
        }

        String jdbcUrl = dataSourceMonitorWrapper.getUrl();
        if (jdbcUrl != null) {
            dataSource.setUrl(jdbcUrl);
        }

        int activeConnectionSize = dataSourceMonitorWrapper.getActiveConnectionSize();
        // this field is optional (default value is 0)
        if (activeConnectionSize != 0) {
            dataSource.setActiveConnectionSize(activeConnectionSize);
        }

        dataSource.setMaxConnectionSize(dataSourceMonitorWrapper.getMaxConnectionSize());

        return dataSource;
    }

}
