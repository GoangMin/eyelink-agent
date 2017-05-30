package com.m2u.eyelink.agent.profiler.monitor.codahale;

import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.*;

import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.m2u.eyelink.context.thrift.TActiveTrace;
import com.m2u.eyelink.context.thrift.TActiveTraceHistogram;

public class DefaultActiveTraceMetricCollector implements ActiveTraceMetricCollector {

    private static final Gauge<TActiveTraceHistogram> UNSUPPORTED_GAUGE = new EmptyGauge<TActiveTraceHistogram>(null);

    private final Gauge<TActiveTraceHistogram> activeTraceHistogramGauge;

    public DefaultActiveTraceMetricCollector(ActiveTraceMetricSet activeTraceMetricSet) {
        if (activeTraceMetricSet == null) {
            throw new NullPointerException("activeTraceMetricSet must not be null");
        }
        Map<String, Metric> metrics = activeTraceMetricSet.getMetrics();
        this.activeTraceHistogramGauge = ((Gauge<TActiveTraceHistogram>) MetricMonitorValues.getMetric(metrics, ACTIVE_TRACE_COUNT, UNSUPPORTED_GAUGE));
    }

    @Override
    public TActiveTrace collect() {
        TActiveTrace activeTrace = new TActiveTrace();
        activeTrace.setHistogram(this.activeTraceHistogramGauge.getValue());
        return activeTrace;
    }

}
