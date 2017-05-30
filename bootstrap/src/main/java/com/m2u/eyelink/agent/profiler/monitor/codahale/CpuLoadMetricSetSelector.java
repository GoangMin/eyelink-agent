package com.m2u.eyelink.agent.profiler.monitor.codahale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.util.JvmUtils;
import com.m2u.eyelink.util.jdk.JvmType;
import com.m2u.eyelink.util.jdk.JvmVersion;

public class CpuLoadMetricSetSelector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CpuLoadMetricSetSelector.class);

    private static final CpuLoadMetricSet DISABLED_CPU_LOAD_METRIC_SET = new EmptyCpuLoadMetricSet();

    // Oracle
    private static final String ORACLE_JDK6_CPU_LOAD_METRIC_SET_FQCN = "com.navercorp.pinpoint.profiler.monitor.codahale.cpu.metric.oracle.Java6CpuLoadMetricSet";
    private static final String ORACLE_CPU_LOAD_METRIC_SET_FQCN = "com.navercorp.pinpoint.profiler.monitor.codahale.cpu.metric.oracle.DefaultCpuLoadMetricSet";
    // IBM
    private static final String IBM_JDK6_CPU_LOAD_METRIC_SET_FQCN = "com.navercorp.pinpoint.profiler.monitor.codahale.cpu.metric.ibm.Java6CpuLoadMetricSet";
    private static final String IBM_CPU_LOAD_METRIC_SET_FQCN = "com.navercorp.pinpoint.profiler.monitor.codahale.cpu.metric.ibm.DefaultCpuLoadMetricSet";

    private CpuLoadMetricSetSelector() {
        throw new IllegalAccessError();
    }

    public static CpuLoadMetricSet getCpuLoadMetricSet(String vendorName) {
        String classToLoad = null;
        JvmType vmType = JvmType.fromVendor(vendorName);
        if (vmType == JvmType.UNKNOWN) {
            vmType = JvmUtils.getType();
        }
        JvmVersion vmVersion = JvmUtils.getVersion();
        if (vmType == JvmType.ORACLE || vmType == JvmType.OPENJDK) {
            if (vmVersion.onOrAfter(JvmVersion.JAVA_7)) {
                classToLoad = ORACLE_CPU_LOAD_METRIC_SET_FQCN;
            } else if (vmVersion.onOrAfter(JvmVersion.JAVA_5)) {
                classToLoad = ORACLE_JDK6_CPU_LOAD_METRIC_SET_FQCN;
            }
        } else if (vmType == JvmType.IBM) {
            if (vmVersion.onOrAfter(JvmVersion.JAVA_7)) {
                classToLoad = IBM_CPU_LOAD_METRIC_SET_FQCN;
            } else if (vmVersion == JvmVersion.JAVA_6) {
                classToLoad = IBM_JDK6_CPU_LOAD_METRIC_SET_FQCN;
            }
        }
        if (classToLoad != null) {
            return createCpuLoadMetricSet(classToLoad);
        } else {
            return DISABLED_CPU_LOAD_METRIC_SET;
        }
    }

    private static CpuLoadMetricSet createCpuLoadMetricSet(String classToLoad) {
        try {
            Class<CpuLoadMetricSet> cpuLoadMetricSetClass = (Class<CpuLoadMetricSet>) Class.forName(classToLoad);
            return cpuLoadMetricSetClass.newInstance();
        } catch (Exception e) {
            LOGGER.error("Error creating CpuLoadMetricSet [{}].", classToLoad, e);
        }
        return DISABLED_CPU_LOAD_METRIC_SET;
    }
}
