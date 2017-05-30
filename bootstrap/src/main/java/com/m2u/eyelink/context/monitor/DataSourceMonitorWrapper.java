package com.m2u.eyelink.context.monitor;

import java.lang.ref.WeakReference;

import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;
import com.m2u.eyelink.trace.ServiceType;

public class DataSourceMonitorWrapper implements PluginMonitorWrapper, DataSourceMonitor {

    private final int id;
    private final WeakReference<DataSourceMonitor> monitorReference;

    private volatile ServiceType serviceType;
    private volatile String name;
    private volatile String url;

    public DataSourceMonitorWrapper(int id, DataSourceMonitor dataSourceMonitor) {
        if (dataSourceMonitor == null) {
            throw new NullPointerException("dataSourceMonitor may not be null");
        }

        this.id = id;
        this.monitorReference = new WeakReference<DataSourceMonitor>(dataSourceMonitor);
    }

    private DataSourceMonitor getInstance() {
        return monitorReference.get();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ServiceType getServiceType() {
        if (this.serviceType != null) {
            return this.serviceType;
        }

        DataSourceMonitor dataSourceMonitor = getInstance();
        if (dataSourceMonitor != null) {
            ServiceType serviceType = dataSourceMonitor.getServiceType();
            if (serviceType != null) {
                this.serviceType = serviceType;
            }
            return serviceType;
        }
        return ServiceType.UNKNOWN;
    }

    @Override
    public String getName() {
        if (this.name != null) {
            return this.name;
        }

        DataSourceMonitor dataSourceMonitor = getInstance();
        if (dataSourceMonitor != null) {
            String name = dataSourceMonitor.getName();
            if (name != null) {
                this.name = name;
            }
            return name;
        }
        return null;
    }

    @Override
    public String getUrl() {
        if (this.url != null) {
            return this.url;
        }

        DataSourceMonitor dataSourceMonitor = getInstance();
        if (dataSourceMonitor != null) {
            String url = dataSourceMonitor.getUrl();
            if (url != null) {
                this.url = url;
            }
            return url;
        }
        return null;
    }

    @Override
    public int getActiveConnectionSize() {
        DataSourceMonitor dataSourceMonitor = getInstance();
        if (dataSourceMonitor != null) {
            return dataSourceMonitor.getActiveConnectionSize();
        }
        return -1;
    }

    @Override
    public int getMaxConnectionSize() {
        DataSourceMonitor dataSourceMonitor = getInstance();
        if (dataSourceMonitor != null) {
            return dataSourceMonitor.getMaxConnectionSize();
        }
        return -1;
    }

    @Override
    public boolean isDisabled() {
        DataSourceMonitor dataSourceMonitor = getInstance();
        if (dataSourceMonitor == null) {
            return true;
        }

        return dataSourceMonitor.isDisabled();
    }

    @Override
    public boolean equalsWithUnwrap(Object object) {
        if (object == null) {
            return false;
        }

        DataSourceMonitor instance = getInstance();
        if (instance == null) {
            return false;
        }

        return instance == object;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataSourceMonitorWrapper{");
        sb.append("id=").append(id);
        sb.append('}');
        return sb.toString();
    }

}
