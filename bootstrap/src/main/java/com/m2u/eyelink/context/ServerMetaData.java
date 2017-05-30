package com.m2u.eyelink.context;

import java.util.List;
import java.util.Map;

public interface ServerMetaData {
    String getServerInfo();
    
    List<String> getVmArgs();

    Map<Integer, String> getConnectors();

    List<ServiceInfo> getServiceInfos();
}
