package com.m2u.eyelink.agent.profiler;

import com.m2u.eyelink.common.Version;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.RuntimeMXBeanUtils;
import com.m2u.eyelink.trace.ServiceType;
import com.m2u.eyelink.util.IdValidateUtils;
import com.m2u.eyelink.util.JvmUtils;
import com.m2u.eyelink.util.NetworkUtils;
import com.m2u.eyelink.util.SystemPropertyKey;

public class AgentInformationFactory {

    private final String agentId;
    private final String applicationName;

    public AgentInformationFactory(String agentId, String applicationName) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }

        this.agentId = checkId(agentId);
        this.applicationName = checkId(applicationName);
    }

    public AgentInformation createAgentInformation(ServiceType serverType) {
        if (serverType == null) {
            throw new NullPointerException("serverType must not be null");
        }
        final String machineName = NetworkUtils.getHostName();
        final String hostIp = NetworkUtils.getRepresentationHostIp();
        final long startTime = RuntimeMXBeanUtils.getVmStartTime();
        final int pid = RuntimeMXBeanUtils.getPid();
        final String jvmVersion = JvmUtils.getSystemProperty(SystemPropertyKey.JAVA_VERSION);
        return new AgentInformation(agentId, applicationName, startTime, pid, machineName, hostIp, serverType, jvmVersion, Version.VERSION);
    }

    private String checkId(String id) {
        if (!IdValidateUtils.validateId(id)) {
            throw new IllegalStateException("invalid Id=" + id);
        }
        return id;
    }


}
