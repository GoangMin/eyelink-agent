package com.m2u.eyelink.agent.profiler.monitor.codahale;

import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.CPU_LOAD_JVM;
import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.CPU_LOAD_SYSTEM;
import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.DOUBLE_ZERO;

import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.m2u.eyelink.context.thrift.TCpuLoad;

public class DefaultCpuLoadCollector implements CpuLoadCollector {

    private final Gauge<Double> jvmCpuLoadGauge;
    private final Gauge<Double> systemCpuLoadGauge;

    @SuppressWarnings("unchecked")
    public DefaultCpuLoadCollector(CpuLoadMetricSet cpuLoadMetricSet) {
        if (cpuLoadMetricSet == null) {
            throw new NullPointerException("cpuLoadMetricSet must not be null");
        }
        Map<String, Metric> metrics = cpuLoadMetricSet.getMetrics();
        this.jvmCpuLoadGauge = (Gauge<Double>) MetricMonitorValues.getMetric(metrics, CPU_LOAD_JVM, DOUBLE_ZERO);
        this.systemCpuLoadGauge = (Gauge<Double>) MetricMonitorValues.getMetric(metrics, CPU_LOAD_SYSTEM, DOUBLE_ZERO);
    }

    @Override
    public TCpuLoad collect() {
        Double jvmCpuLoad = this.jvmCpuLoadGauge.getValue();
        Double systemCpuLoad = this.systemCpuLoadGauge.getValue();
        if (notCollected(jvmCpuLoad) && notCollected(systemCpuLoad)) {
            return null;
        }
        TCpuLoad cpuLoad = new TCpuLoad();
        if (!notCollected(jvmCpuLoad)) {
            cpuLoad.setJvmCpuLoad(jvmCpuLoad);
        }
        if (!notCollected(systemCpuLoad)) {
            cpuLoad.setSystemCpuLoad(systemCpuLoad);
        }
        return cpuLoad;
    }

    private boolean notCollected(double cpuLoad) {
        return cpuLoad < 0 || Double.isNaN(cpuLoad);
    }
}
