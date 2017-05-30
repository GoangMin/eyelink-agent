package com.m2u.eyelink.common.service;

import java.util.List;

import com.m2u.eyelink.trace.ServiceType;


public interface ServiceTypeRegistryService {
    ServiceType findServiceType(short serviceType);

    ServiceType findServiceTypeByName(String typeName);

    @Deprecated
    List<ServiceType> findDesc(String desc);
}
