package com.m2u.eyelink.plugin.monitor;

import com.m2u.eyelink.trace.ServiceType;


public interface DataSourceMonitor {
    ServiceType getServiceType();

    String getName();

    String getUrl();

    int getActiveConnectionSize();

    int getMaxConnectionSize();

    boolean isDisabled();
}
