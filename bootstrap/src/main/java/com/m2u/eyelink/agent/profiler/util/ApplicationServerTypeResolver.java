package com.m2u.eyelink.agent.profiler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.plugin.ApplicationTypeDetector;
import com.m2u.eyelink.agent.profiler.plugin.DefaultProfilerPluginContext;
import com.m2u.eyelink.resolver.ApplicationServerTypePluginResolver;
import com.m2u.eyelink.trace.ServiceType;

public class ApplicationServerTypeResolver {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServiceType defaultType;
    private final ApplicationServerTypePluginResolver resolver;
    private final List<ApplicationTypeDetector> detectors = new ArrayList<ApplicationTypeDetector>();

    public ApplicationServerTypeResolver(List<DefaultProfilerPluginContext> plugins, ServiceType defaultType, List<String> orderedDetectors) {
        if (isValidApplicationServerType(defaultType)) {
            this.defaultType = defaultType;
        } else {
            this.defaultType = ServiceType.UNDEFINED;
        }
        Map<String, ApplicationTypeDetector> registeredDetectors = getRegisteredServerTypeDetectors(plugins);
        for (String orderedDetector : orderedDetectors) {
            if (registeredDetectors.containsKey(orderedDetector)) {
                this.detectors.add(registeredDetectors.remove(orderedDetector));
            }
        }
        this.detectors.addAll(registeredDetectors.values());
        this.resolver = new ApplicationServerTypePluginResolver(this.detectors);
    }
    
    private Map<String, ApplicationTypeDetector> getRegisteredServerTypeDetectors(List<DefaultProfilerPluginContext> plugins) {
        Map<String, ApplicationTypeDetector> registeredDetectors = new HashMap<String, ApplicationTypeDetector>();
        for (DefaultProfilerPluginContext context : plugins) {
            for (ApplicationTypeDetector detector : context.getApplicationTypeDetectors()) {
                registeredDetectors.put(detector.getClass().getName(), detector);
            }
        }
        return registeredDetectors;
    }
    
    public ServiceType resolve() {
        ServiceType resolvedApplicationServerType;
        if (this.defaultType == ServiceType.UNDEFINED) {
            resolvedApplicationServerType = this.resolver.resolve();
            logger.info("Resolved ApplicationServerType : {}", resolvedApplicationServerType.getName());
        } else {
            resolvedApplicationServerType = this.defaultType;
            logger.info("Configured ApplicationServerType : {}", resolvedApplicationServerType.getName());
        }
        return resolvedApplicationServerType;
    }
    
    private boolean isValidApplicationServerType(ServiceType serviceType) {
        if (serviceType == null) {
            return false;
        }
        return serviceType.isWas();
    }
}
