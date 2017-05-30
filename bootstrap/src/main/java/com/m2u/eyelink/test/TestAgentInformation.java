package com.m2u.eyelink.test;

import com.m2u.eyelink.common.Version;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.trace.ServiceType;
import com.m2u.eyelink.util.SystemPropertyKey;
import com.m2u.eyelink.util.jdk.JvmUtils;

public class TestAgentInformation extends AgentInformation {
    
    private static final String AGENT_ID = "test-agent";
    private static final String APPLICATION_NAME = "TEST_APPLICATION";
    private static final int PID = 10;
    private static final String MACHINE_NAME = "test-machine";
    private static final String HOST_IP = "127.0.0.1";
    private static final ServiceType SERVICE_TYPE = ServiceType.TEST_STAND_ALONE;
    private static final String JVM_VERSION = JvmUtils.getSystemProperty(SystemPropertyKey.JAVA_VERSION);
    private static final String AGENT_VERSION = Version.VERSION;

    public TestAgentInformation() {
        super(AGENT_ID, APPLICATION_NAME, System.currentTimeMillis(), PID, MACHINE_NAME, HOST_IP, SERVICE_TYPE, JVM_VERSION, AGENT_VERSION);
    }
}
