package com.m2u.eyelink.agent;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.List;

import com.m2u.eyelink.common.service.AnnotationKeyRegistryService;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.config.ProfilerConfig;


public interface AgentOption {

    Instrumentation getInstrumentation();

    String getAgentId();

    String getApplicationName();

    ProfilerConfig getProfilerConfig();

    URL[] getPluginJars();

    List<String> getBootstrapJarPaths();

    ServiceTypeRegistryService getServiceTypeRegistryService();
    
    AnnotationKeyRegistryService getAnnotationKeyRegistryService();
}
