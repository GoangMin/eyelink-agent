package com.m2u.eyelink.agent.profiler.monitor.codahale;

import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.*;

import java.util.SortedMap;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.m2u.eyelink.agent.profiler.monitor.GarbageCollector;
import com.m2u.eyelink.agent.profiler.monitor.MetricMonitorRegistry;
import com.m2u.eyelink.context.thrift.TJvmGc;
import com.m2u.eyelink.context.thrift.TJvmGcDetailed;

public class G1DetailedMetricsCollector extends G1Collector {

    private final Gauge<Double> codeCacheUsage;
    private final Gauge<Double> newGenUsage;
    private final Gauge<Double> oldGenUsage;
    private final Gauge<Double> survivorUsage;
    private final Gauge<Double> permGenUsage;
    private final Gauge<Double> metaspaceUsage;
    private final Gauge<Long> newGcCount;
    private final Gauge<Long> newGcTime;

    public G1DetailedMetricsCollector(MetricMonitorRegistry registry) {

        super(registry);

        final MetricRegistry metricRegistry = registry.getRegistry();
        @SuppressWarnings("rawtypes")
        final SortedMap<String, Gauge> gauges = metricRegistry.getGauges();

        this.codeCacheUsage = getDoubleGauge(gauges, JVM_MEMORY_POOLS_G1_CODE_CACHE_USAGE);
        this.newGenUsage = getDoubleGauge(gauges, JVM_MEMORY_POOLS_G1_NEWGEN_USAGE);
        this.oldGenUsage = getDoubleGauge(gauges, JVM_MEMORY_POOLS_G1_OLDGEN_USAGE);
        this.survivorUsage = getDoubleGauge(gauges, JVM_MEMORY_POOLS_G1_SURVIVOR_USAGE);

        if (gauges.containsKey(JVM_MEMORY_POOLS_G1_PERMGEN_USAGE)) {
            this.permGenUsage = getDoubleGauge(gauges, JVM_MEMORY_POOLS_G1_PERMGEN_USAGE);
            this.metaspaceUsage = EXCLUDED_DOUBLE;
        } else {
            this.metaspaceUsage = getDoubleGauge(gauges, JVM_MEMORY_POOLS_G1_METASPACE_USAGE);
            this.permGenUsage = EXCLUDED_DOUBLE;
        }

        this.newGcCount = getLongGauge(gauges, JVM_GC_G1_NEWGEN_COUNT);
        this.newGcTime = getLongGauge(gauges, JVM_GC_G1_NEWGEN_TIME);

    }

    @Override
    public TJvmGc collect() {
        final TJvmGc gc = super.collect();
        final TJvmGcDetailed details = new TJvmGcDetailed();
        details.setJvmPoolCodeCacheUsed(codeCacheUsage.getValue());
        details.setJvmPoolNewGenUsed(newGenUsage.getValue());
        details.setJvmPoolOldGenUsed(oldGenUsage.getValue());
        details.setJvmPoolSurvivorSpaceUsed(survivorUsage.getValue());
        if (EXCLUDED_DOUBLE.getValue() == permGenUsage.getValue()) {
            // metric for jvm >= 1.8
            details.setJvmPoolMetaspaceUsed(metaspaceUsage.getValue());
        } else {
            // metric for jvm < 1.8
            details.setJvmPoolPermGenUsed(permGenUsage.getValue());
        }
        details.setJvmGcNewCount(newGcCount.getValue());
        details.setJvmGcNewTime(newGcTime.getValue());
        gc.setJvmGcDetailed(details);
        return gc;
    }

    @Override
    public String toString() {
        return "HotSpot's Garbage-First(G1) collector with detailed metrics";
    }

}
