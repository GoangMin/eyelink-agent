package com.m2u.eyelink.agent.profiler.monitor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.m2u.eyelink.agent.profiler.context.TransactionCounter;
import com.m2u.eyelink.agent.profiler.monitor.codahale.ActiveTraceMetricSet;
import com.m2u.eyelink.agent.profiler.monitor.codahale.CpuLoadMetricSet;
import com.m2u.eyelink.agent.profiler.monitor.codahale.CpuLoadMetricSetSelector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DataSourceMetricSet;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricCounterMonitor;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricEventRateMonitor;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricHistogramMonitor;
import com.m2u.eyelink.agent.profiler.monitor.codahale.TransactionMetricSet;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.context.monitor.DataSourceMonitorWrapper;
import com.m2u.eyelink.context.monitor.PluginMonitorWrapperLocator;

public class MetricMonitorRegistry implements MonitorRegistry {

	private final MetricRegistry delegate;

	public MetricMonitorRegistry() {
		this(new MetricRegistry());
	}

	public MetricMonitorRegistry(MetricRegistry registry) {
		if (registry == null) {
			throw new NullPointerException("registry is null");
		}
		this.delegate = registry;
	}

	public HistogramMonitor newHistogramMonitor(MonitorName monitorName) {
		validateMonitorName(monitorName);
		final Histogram histogram = this.delegate.histogram(monitorName
				.getName());
		return new MetricHistogramMonitor(histogram);
	}

	public EventRateMonitor newEventRateMonitor(MonitorName monitorName) {
		validateMonitorName(monitorName);
		final Meter meter = this.delegate.meter(monitorName.getName());
		return new MetricEventRateMonitor(meter);
	}

	public CounterMonitor newCounterMonitor(MonitorName monitorName) {
		validateMonitorName(monitorName);
		final Counter counter = this.delegate.counter(monitorName.getName());
		return new MetricCounterMonitor(counter);
	}

	public MemoryUsageGaugeSet registerJvmMemoryMonitor(MonitorName monitorName) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new MemoryUsageGaugeSet());
	}

	public JvmAttributeGaugeSet registerJvmAttributeMonitor(
			MonitorName monitorName) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new JvmAttributeGaugeSet());
	}

	public GarbageCollectorMetricSet registerJvmGcMonitor(
			MonitorName monitorName) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new GarbageCollectorMetricSet());
	}

	public CpuLoadMetricSet registerCpuLoadMonitor(MonitorName monitorName,
			String vendorName) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				CpuLoadMetricSetSelector.getCpuLoadMetricSet(vendorName));
	}

	public TransactionMetricSet registerTpsMonitor(MonitorName monitorName,
			TransactionCounter transactionCounter) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new TransactionMetricSet(transactionCounter));
	}

	public ActiveTraceMetricSet registerActiveTraceMetricSet(
			MonitorName monitorName, ActiveTraceLocator activeTraceLocator) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new ActiveTraceMetricSet(activeTraceLocator));
	}

	public ThreadStatesGaugeSet registerJvmThreadStatesMonitor(
			MonitorName monitorName) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new ThreadStatesGaugeSet());
	}

	public DataSourceMetricSet registerDataSourceMonitor(
			MonitorName monitorName,
			PluginMonitorWrapperLocator<DataSourceMonitorWrapper> dataSourceMonitorLocator) {
		validateMonitorName(monitorName);
		return this.delegate.register(monitorName.getName(),
				new DataSourceMetricSet(dataSourceMonitorLocator));
	}

	public MetricRegistry getRegistry() {
		return this.delegate;
	}

	public String toString() {
		return "MetricMonitorRegistry(delegate=" + this.delegate + ")";
	}

	private void validateMonitorName(MonitorName monitorName) {
		if (monitorName == null) {
			throw new NullPointerException("monitorName must not be null");
		}
	}

}
