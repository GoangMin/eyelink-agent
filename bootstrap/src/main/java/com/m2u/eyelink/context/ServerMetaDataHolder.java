package com.m2u.eyelink.context;

import java.util.List;


public interface ServerMetaDataHolder {
    void setServerName(String serverName);
    
    void addConnector(String protocol, int port);
    
    void addServiceInfo(String serviceName, List<String> serviceLibs);
    
    void addListener(ServerMetaDataListener listener);
    
    void removeListener(ServerMetaDataListener listener);
    
    void notifyListeners();
    
    interface ServerMetaDataListener {
        
        void publishServerMetaData(ServerMetaData serverMetaData);
        
    }
}
