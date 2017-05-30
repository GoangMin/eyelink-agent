package com.m2u.eyelink.agent.profiler.receiver.service;

import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandService;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandServiceGroup;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.ActiveTraceLocator;

public class ActiveThreadService implements ProfilerCommandServiceGroup {

    private final List<ProfilerCommandService> serviceList;

    public ActiveThreadService(ActiveTraceLocator activeTraceLocator, ProfilerConfig profilerConfig) {
        serviceList = new ArrayList<ProfilerCommandService>();

        if (!profilerConfig.isTcpDataSenderCommandActiveThreadEnable()) {
            return;
        }

        if (profilerConfig.isTcpDataSenderCommandActiveThreadCountEnable()) {
            serviceList.add(new ActiveThreadCountService(activeTraceLocator));
        }
        if (profilerConfig.isTcpDataSenderCommandActiveThreadLightDumpEnable()) {
            serviceList.add(new ActiveThreadLightDumpService(activeTraceLocator));
        }
        if (profilerConfig.isTcpDataSenderCommandActiveThreadDumpEnable()) {
            serviceList.add(new ActiveThreadDumpService(activeTraceLocator));
        }
    }

    @Override
    public List<ProfilerCommandService> getCommandServiceList() {
        return serviceList;
    }

}
