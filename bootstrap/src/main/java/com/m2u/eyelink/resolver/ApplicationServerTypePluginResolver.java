package com.m2u.eyelink.resolver;

import java.util.List;

import com.m2u.eyelink.agent.profiler.plugin.ApplicationTypeDetector;
import com.m2u.eyelink.agent.resolver.ConditionProvider;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.trace.ServiceType;

public class ApplicationServerTypePluginResolver {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private final List<ApplicationTypeDetector> applicationTypeDetectors;
    
    private final ConditionProvider conditionProvider;
    
    private static final ServiceType DEFAULT_SERVER_TYPE = ServiceType.STAND_ALONE;
    
    public ApplicationServerTypePluginResolver(List<ApplicationTypeDetector> serverTypeDetectors) {
        this(serverTypeDetectors, ConditionProvider.DEFAULT_CONDITION_PROVIDER);
    }
    
    public ApplicationServerTypePluginResolver(List<ApplicationTypeDetector> serverTypeDetectors, ConditionProvider conditionProvider) {
        if (serverTypeDetectors == null) {
            throw new IllegalArgumentException("applicationTypeDetectors should not be null");
        }
        if (conditionProvider == null) {
            throw new IllegalArgumentException("conditionProvider should not be null");
        }
        this.applicationTypeDetectors = serverTypeDetectors;
        this.conditionProvider = conditionProvider;
    }

    public ServiceType resolve() {
        for (ApplicationTypeDetector currentDetector : this.applicationTypeDetectors) {
            String currentDetectorName = currentDetector.getClass().getName();
            logger.info("Attempting to resolve using [{}]", currentDetectorName);
            if (currentDetector.detect(this.conditionProvider)) {
                logger.info("Match found using [{}]", currentDetectorName);
                return currentDetector.getApplicationType();
            } else {
                logger.info("No match found using [{}]", currentDetectorName);
            }
        }
        logger.debug("Server type not resolved. Defaulting to {}", DEFAULT_SERVER_TYPE.getName());
        return DEFAULT_SERVER_TYPE;
    }
}
