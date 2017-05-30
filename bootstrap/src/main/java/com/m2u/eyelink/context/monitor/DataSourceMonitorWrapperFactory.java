package com.m2u.eyelink.context.monitor;

import java.util.concurrent.atomic.AtomicInteger;

import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;

public class DataSourceMonitorWrapperFactory {
	   private final AtomicInteger idGenerator = new AtomicInteger();

	    public DataSourceMonitorWrapper create(DataSourceMonitor pluginMonitor) {
	        return new DataSourceMonitorWrapper(idGenerator.incrementAndGet(), pluginMonitor);
	    }

	    public int latestIssuedId() {
	        return idGenerator.get();
	    }
}
