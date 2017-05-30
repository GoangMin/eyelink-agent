package com.m2u.eyelink.agent.profiler.monitor.codahale;

import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.*;

import java.util.SortedMap;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.m2u.eyelink.agent.profiler.monitor.GarbageCollector;
import com.m2u.eyelink.agent.profiler.monitor.MetricMonitorRegistry;
import com.m2u.eyelink.context.thrift.TJvmGc;
import com.m2u.eyelink.context.thrift.TJvmGcType;

public class ParallelCollector implements GarbageCollector {

    public static final TJvmGcType GC_TYPE = TJvmGcType.PARALLEL;

    private final Gauge<Long> heapMax;
    private final Gauge<Long> heapUsed;

    private final Gauge<Long> heapNonHeapMax;
    private final Gauge<Long> heapNonHeapUsed;

    private final Gauge<Long> oldGcCount;
    private final Gauge<Long> oldGcTime;

    public ParallelCollector(MetricMonitorRegistry registry) {

        if (registry == null) {
            throw new NullPointerException("registry must not be null");
        }

        final MetricRegistry metricRegistry = registry.getRegistry();
        @SuppressWarnings("rawtypes")
        final SortedMap<String, Gauge> gauges = metricRegistry.getGauges();

        this.heapMax = getLongGauge(gauges, JVM_MEMORY_HEAP_MAX);
        this.heapUsed = getLongGauge(gauges, JVM_MEMORY_HEAP_USED);

        this.heapNonHeapMax = getLongGauge(gauges, JVM_MEMORY_NONHEAP_MAX);
        this.heapNonHeapUsed = getLongGauge(gauges, JVM_MEMORY_NONHEAP_USED);

        this.oldGcCount = getLongGauge(gauges, JVM_GC_PS_OLDGEN_COUNT);
        this.oldGcTime = getLongGauge(gauges, JVM_GC_PS_OLDGEN_TIME);

    }

    @Override
    public int getTypeCode() {
        return GC_TYPE.getValue();
    }

    @Override
    public TJvmGc collect() {

        final TJvmGc gc = new TJvmGc();
        gc.setType(GC_TYPE);
        gc.setJvmMemoryHeapMax(heapMax.getValue());
        gc.setJvmMemoryHeapUsed(heapUsed.getValue());
        gc.setJvmMemoryNonHeapMax(heapNonHeapMax.getValue());
        gc.setJvmMemoryNonHeapUsed(heapNonHeapUsed.getValue());
        gc.setJvmGcOldCount(oldGcCount.getValue());
        gc.setJvmGcOldTime(oldGcTime.getValue());

        return gc;
    }

    @Override
    public String toString() {
        return "HotSpot's Parallel (Old) collector";
    }

}
