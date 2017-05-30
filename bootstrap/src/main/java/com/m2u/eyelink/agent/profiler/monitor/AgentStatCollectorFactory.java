package com.m2u.eyelink.agent.profiler.monitor;

import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.JVM_GC_CMS_OLDGEN_COUNT;
import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.JVM_GC_G1_OLDGEN_COUNT;
import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.JVM_GC_PS_OLDGEN_COUNT;
import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.JVM_GC_SERIAL_OLDGEN_COUNT;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.context.TransactionCounter;
import com.m2u.eyelink.agent.profiler.monitor.codahale.ActiveTraceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.ActiveTraceMetricSet;
import com.m2u.eyelink.agent.profiler.monitor.codahale.CmsCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.CmsDetailedMetricsCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.CpuLoadCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.CpuLoadMetricSet;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DataSourceCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DataSourceMetricSet;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DefaultActiveTraceMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DefaultCpuLoadCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DefaultDataSourceCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.DefaultTransactionMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.G1Collector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.G1DetailedMetricsCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues;
import com.m2u.eyelink.agent.profiler.monitor.codahale.ParallelCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.ParallelDetailedMetricsCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.SerialCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.SerialDetailedMetricsCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.TransactionMetricCollector;
import com.m2u.eyelink.agent.profiler.monitor.codahale.TransactionMetricSet;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.context.DefaultTraceContext;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.context.monitor.DataSourceMonitorWrapper;
import com.m2u.eyelink.context.monitor.DefaultPluginMonitorContext;
import com.m2u.eyelink.context.monitor.PluginMonitorWrapperLocator;
import com.m2u.eyelink.plugin.monitor.PluginMonitorContext;
import com.m2u.eyelink.plugin.tomcat.DefaultProfilerConfig;

public class AgentStatCollectorFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MetricMonitorRegistry monitorRegistry;
    private final GarbageCollector garbageCollector;
    private final CpuLoadCollector cpuLoadCollector;
    private final TransactionMetricCollector transactionMetricCollector;
    private final ActiveTraceMetricCollector activeTraceMetricCollector;
    private final DataSourceCollector dataSourceCollector;

    public AgentStatCollectorFactory(TraceContext traceContext) {
        if (traceContext == null) {
            throw new NullPointerException("traceContext must not be null");
        }
        ProfilerConfig profilerConfig = traceContext.getProfilerConfig();
        if (profilerConfig == null) {
            profilerConfig = new DefaultProfilerConfig();
        }
        this.monitorRegistry = createRegistry();
        this.garbageCollector = createGarbageCollector(profilerConfig.isProfilerJvmCollectDetailedMetrics());
        this.cpuLoadCollector = createCpuLoadCollector(profilerConfig.getProfilerJvmVendorName());
        this.transactionMetricCollector = createTransactionMetricCollector(traceContext);
        this.activeTraceMetricCollector = createActiveTraceCollector(traceContext, profilerConfig.isTraceAgentActiveThread());
        this.dataSourceCollector = createDataSourceCollector(traceContext);
    }

    private MetricMonitorRegistry createRegistry() {
        final MetricMonitorRegistry monitorRegistry = new MetricMonitorRegistry();
        return monitorRegistry;
    }

    /**
     * create with garbage collector types based on metric registry keys
     */
    private GarbageCollector createGarbageCollector(boolean collectDetailedMetrics) {
        MetricMonitorRegistry registry = this.monitorRegistry;
        registry.registerJvmMemoryMonitor(new MonitorName(MetricMonitorValues.JVM_MEMORY));
        registry.registerJvmGcMonitor(new MonitorName(MetricMonitorValues.JVM_GC));

        Collection<String> keys = registry.getRegistry().getNames();
        GarbageCollector garbageCollectorToReturn = new UnknownGarbageCollector();

        if (collectDetailedMetrics) {
            if (keys.contains(JVM_GC_SERIAL_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new SerialDetailedMetricsCollector(registry);
            } else if (keys.contains(JVM_GC_PS_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new ParallelDetailedMetricsCollector(registry);
            } else if (keys.contains(JVM_GC_CMS_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new CmsDetailedMetricsCollector(registry);
            } else if (keys.contains(JVM_GC_G1_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new G1DetailedMetricsCollector(registry);
            }
        } else {
            if (keys.contains(JVM_GC_SERIAL_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new SerialCollector(registry);
            } else if (keys.contains(JVM_GC_PS_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new ParallelCollector(registry);
            } else if (keys.contains(JVM_GC_CMS_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new CmsCollector(registry);
            } else if (keys.contains(JVM_GC_G1_OLDGEN_COUNT)) {
                garbageCollectorToReturn = new G1Collector(registry);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("found : {}", garbageCollectorToReturn);
        }
        return garbageCollectorToReturn;
    }

    private CpuLoadCollector createCpuLoadCollector(String vendorName) {
        CpuLoadMetricSet cpuLoadMetricSet = this.monitorRegistry.registerCpuLoadMonitor(new MonitorName(MetricMonitorValues.CPU_LOAD), vendorName);
        if (logger.isInfoEnabled()) {
            logger.info("loaded : {}", cpuLoadMetricSet);
        }
        return new DefaultCpuLoadCollector(cpuLoadMetricSet);
    }

    private TransactionMetricCollector createTransactionMetricCollector(TraceContext traceContext) {
        if (traceContext instanceof DefaultTraceContext) {
            TransactionCounter transactionCounter = ((DefaultTraceContext) traceContext).getTransactionCounter();
            TransactionMetricSet transactionMetricSet = this.monitorRegistry.registerTpsMonitor(new MonitorName(MetricMonitorValues.TRANSACTION), transactionCounter);
            if (logger.isInfoEnabled()) {
                logger.info("loaded : {}", transactionMetricSet);
            }
            return new DefaultTransactionMetricCollector(transactionMetricSet);
        } else {
            return TransactionMetricCollector.EMPTY_TRANSACTION_METRIC_COLLECTOR;
        }
    }

    private ActiveTraceMetricCollector createActiveTraceCollector(TraceContext traceContext, boolean isTraceAgentActiveThread) {
        if (!isTraceAgentActiveThread) {
            return ActiveTraceMetricCollector.EMPTY_ACTIVE_TRACE_COLLECTOR;
        }
        if (traceContext instanceof DefaultTraceContext) {
            ActiveTraceLocator activeTraceLocator = ((DefaultTraceContext) traceContext).getActiveTraceLocator();
            if (activeTraceLocator != null) {
                ActiveTraceMetricSet activeTraceMetricSet = this.monitorRegistry.registerActiveTraceMetricSet(new MonitorName(MetricMonitorValues.ACTIVE_TRACE), activeTraceLocator);
                if (logger.isInfoEnabled()) {
                    logger.info("loaded : {}", activeTraceMetricSet);
                }
                return new DefaultActiveTraceMetricCollector(activeTraceMetricSet);
            } else {
                logger.warn("agent set to trace active threads but no ActiveTraceLocator found");
            }
        }
        return ActiveTraceMetricCollector.EMPTY_ACTIVE_TRACE_COLLECTOR;
    }

    private DataSourceCollector createDataSourceCollector(TraceContext traceContext) {
        if (traceContext instanceof DefaultTraceContext) {
            PluginMonitorContext pluginMonitorContext = traceContext.getPluginMonitorContext();
            if (pluginMonitorContext instanceof DefaultPluginMonitorContext) {
                PluginMonitorWrapperLocator<DataSourceMonitorWrapper> dataSourceMonitorLocator = ((DefaultPluginMonitorContext) pluginMonitorContext).getDataSourceMonitorLocator();
                if (dataSourceMonitorLocator != null) {
                    DataSourceMetricSet dataSourceMetricSet = this.monitorRegistry.registerDataSourceMonitor(new MonitorName(MetricMonitorValues.DATASOURCE), dataSourceMonitorLocator);
                    return new DefaultDataSourceCollector(dataSourceMetricSet);
                }
            }
        }
        return DataSourceCollector.EMPTY_DATASOURCE_COLLECTOR;
    }

    public GarbageCollector getGarbageCollector() {
        return this.garbageCollector;
    }

    public CpuLoadCollector getCpuLoadCollector() {
        return this.cpuLoadCollector;
    }

    public TransactionMetricCollector getTransactionMetricCollector() {
        return this.transactionMetricCollector;
    }

    public ActiveTraceMetricCollector getActiveTraceMetricCollector() {
        return this.activeTraceMetricCollector;
    }

    public DataSourceCollector getDataSourceCollector() {
        return this.dataSourceCollector;
    }

}
