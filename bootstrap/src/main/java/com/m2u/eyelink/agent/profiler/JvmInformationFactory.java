package com.m2u.eyelink.agent.profiler;

import com.m2u.eyelink.agent.profiler.monitor.GarbageCollector;
import com.m2u.eyelink.agent.profiler.monitor.UnknownGarbageCollector;
import com.m2u.eyelink.util.SystemPropertyKey;
import com.m2u.eyelink.util.jdk.JvmUtils;

public class JvmInformationFactory {

    private final String jvmVersion;
    private final GarbageCollector garbageCollector;

    JvmInformationFactory() {
        this(null);
    }

    public JvmInformationFactory(GarbageCollector garbageCollector) {
        this.jvmVersion = JvmUtils.getSystemProperty(SystemPropertyKey.JAVA_VERSION);
        if (garbageCollector == null) {
            this.garbageCollector = new UnknownGarbageCollector();
        } else {
            this.garbageCollector = garbageCollector;
        }
    }

    public JvmInformation createJvmInformation() {
        return new JvmInformation(this.jvmVersion, this.garbageCollector.getTypeCode());
    }
}
