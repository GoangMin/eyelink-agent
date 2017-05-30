package com.m2u.eyelink.agent.profiler.receiver;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilerCommandLocatorBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Class<? extends TBase>, ProfilerCommandService> profilerCommandServiceRepository;

    public ProfilerCommandLocatorBuilder() {
        profilerCommandServiceRepository = new HashMap<Class<? extends TBase>, ProfilerCommandService>();
    }

    public void addService(ProfilerCommandServiceGroup serviceGroup) {
        if (serviceGroup == null) {
            throw new NullPointerException("serviceGroup must not be null");
        }

        for (ProfilerCommandService service : serviceGroup.getCommandServiceList()) {
            addService(service);
        }
    }

    public boolean addService(ProfilerCommandService service) {
        if (service == null) {
            throw new NullPointerException("service must not be null");
        }
        return addService(service.getCommandClazz(), service);
    }

    public boolean addService(Class<? extends TBase> clazz, ProfilerCommandService service) {
        if (clazz == null) {
            throw new NullPointerException("clazz must not be null");
        }
        if (service == null) {
            throw new NullPointerException("service must not be null");
        }

        boolean hasValue = profilerCommandServiceRepository.containsKey(clazz);
        if (!hasValue) {
            profilerCommandServiceRepository.put(clazz, service);
            return true;
        } else {
            ProfilerCommandService registeredService = profilerCommandServiceRepository.get(clazz);
            logger.warn("Already Register ServiceTypeInfo:{}, RegisteredService:{}.", clazz.getName(), registeredService);
            return false;
        }
    }

    public ProfilerCommandServiceLocator build() {
        return new DefaultProfilerCommandServiceLocator(this);
    }

    protected Map<Class<? extends TBase>, ProfilerCommandService> getProfilerCommandServiceRepository() {
        return profilerCommandServiceRepository;
    }

}
