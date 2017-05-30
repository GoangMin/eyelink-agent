package com.m2u.eyelink.agent.profiler.monitor.codahale;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory;
import com.m2u.eyelink.agent.profiler.context.ActiveTraceHistogramFactory.ActiveTraceHistogram;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.context.thrift.TActiveTraceHistogram;

public class ActiveTraceMetricSet implements MetricSet {

    private final Gauge<TActiveTraceHistogram> activeTraceGauge;

    public ActiveTraceMetricSet(ActiveTraceLocator activeTraceLocator) {
        if (activeTraceLocator == null) {
            throw new NullPointerException("activeTraceLocator must not be null");
        }
        this.activeTraceGauge = new ActiveTraceHistogramGauge(activeTraceLocator);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<String, Metric>();
        gauges.put(MetricMonitorValues.ACTIVE_TRACE_COUNT, this.activeTraceGauge);
        return gauges;
    }

    @Override
    public String toString() {
        return "Default ActiveTraceCountMetricSet";
    }

    private static class ActiveTraceHistogramGauge implements Gauge<TActiveTraceHistogram> {

        private final ActiveTraceHistogramFactory activeTraceHistogramFactory;

        private ActiveTraceHistogramGauge(ActiveTraceLocator activeTraceLocator) {
            this.activeTraceHistogramFactory = new ActiveTraceHistogramFactory(activeTraceLocator);
        }

        @Override
        public TActiveTraceHistogram getValue() {
            ActiveTraceHistogram activeTraceHistogram = this.activeTraceHistogramFactory.createHistogram();
            TActiveTraceHistogram tActiveTraceHistogram = new TActiveTraceHistogram();
            tActiveTraceHistogram.setHistogramSchemaType(activeTraceHistogram.getHistogramSchema().getTypeCode());
            tActiveTraceHistogram.setActiveTraceCount(activeTraceHistogram.getActiveTraceCounts());
            return tActiveTraceHistogram;
        }
    }

}
