package com.m2u.eyelink.agent.profiler.monitor.codahale;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

public abstract class CpuLoadMetricSet implements MetricSet {

    protected abstract Gauge<Double> getJvmCpuLoadGauge();

    protected abstract Gauge<Double> getSystemCpuLoadGauge();

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<String, Metric>();
        gauges.put(MetricMonitorValues.CPU_LOAD_JVM, getJvmCpuLoadGauge());
        gauges.put(MetricMonitorValues.CPU_LOAD_SYSTEM, getSystemCpuLoadGauge());
        return Collections.unmodifiableMap(gauges);
    }

}
