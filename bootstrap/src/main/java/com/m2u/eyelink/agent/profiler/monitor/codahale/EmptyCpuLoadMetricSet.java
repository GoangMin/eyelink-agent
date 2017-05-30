package com.m2u.eyelink.agent.profiler.monitor.codahale;

import com.codahale.metrics.Gauge;

public final class EmptyCpuLoadMetricSet extends CpuLoadMetricSet {

    private static final Double UNSUPPORTED_CPU_LOAD_METRIC = -1.0d;
    private static final Gauge<Double> UNSUPPORTED_CPU_LOAD_METRIC_GAUGE = new Gauge<Double>() {
        @Override
        public Double getValue() {
            return UNSUPPORTED_CPU_LOAD_METRIC;
        }
    };

    @Override
    protected Gauge<Double> getJvmCpuLoadGauge() {
        return UNSUPPORTED_CPU_LOAD_METRIC_GAUGE;
    }

    @Override
    protected Gauge<Double> getSystemCpuLoadGauge() {
        return UNSUPPORTED_CPU_LOAD_METRIC_GAUGE;
    }

    @Override
    public String toString() {
        return "Disabled CpuLoadMetricSet";
    }

}
