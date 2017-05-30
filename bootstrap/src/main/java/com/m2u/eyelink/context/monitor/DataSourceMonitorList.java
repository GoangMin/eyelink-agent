package com.m2u.eyelink.context.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.plugin.monitor.DataSourceMonitor;
import com.m2u.eyelink.plugin.monitor.PluginMonitorRegistry;

public class DataSourceMonitorList implements PluginMonitorRegistry<DataSourceMonitor>, PluginMonitorWrapperLocator<DataSourceMonitorWrapper> {

    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    private final boolean loggerInfoEnabled = logger.isInfoEnabled();

    private final int limitIdNumber;

    private final CopyOnWriteArrayList<DataSourceMonitorWrapper> repository = new CopyOnWriteArrayList<DataSourceMonitorWrapper>();

    private final DataSourceMonitorWrapperFactory wrapperFactory = new DataSourceMonitorWrapperFactory();

    public DataSourceMonitorList(int limitIdNumber) {
        this.limitIdNumber = limitIdNumber;
    }

    @Override
    public boolean register(DataSourceMonitor dataSourceMonitor) {
        if (wrapperFactory.latestIssuedId() >= limitIdNumber) {
            if (loggerInfoEnabled) {
                logger.info("can't register {}. The maximum value of id number has been exceeded.");
            }
            return false;
        }

        DataSourceMonitorWrapper dataSourceMonitorWrapper = wrapperFactory.create(dataSourceMonitor);
        return repository.add(dataSourceMonitorWrapper);
    }

    @Override
    public boolean unregister(DataSourceMonitor dataSourceMonitor) {
        for (DataSourceMonitorWrapper dataSourceMonitorWrapper : repository) {
            if (dataSourceMonitorWrapper.equalsWithUnwrap(dataSourceMonitor)) {
                return repository.remove(dataSourceMonitorWrapper);
            }
        }
        return false;
    }

    @Override
    public List<DataSourceMonitorWrapper> getPluginMonitorWrapperList() {
        List<DataSourceMonitorWrapper> pluginMonitorList = new ArrayList<DataSourceMonitorWrapper>(repository.size());
        List<DataSourceMonitorWrapper> disabledPluginMonitorList = new ArrayList<DataSourceMonitorWrapper>();

        for (DataSourceMonitorWrapper dataSourceMonitorWrapper : repository) {
            if (dataSourceMonitorWrapper.isDisabled()) {
                disabledPluginMonitorList.add(dataSourceMonitorWrapper);
            } else {
                pluginMonitorList.add(dataSourceMonitorWrapper);
            }
        }

        // bulk delete for reduce copy
        if (disabledPluginMonitorList.size() > 0) {
            logger.info("PluginMonitorWrapper was disabled(list:{})", disabledPluginMonitorList);
            repository.removeAll(disabledPluginMonitorList);
        }

        return pluginMonitorList;
    }

    public int getRemainingIdNumber() {
        return limitIdNumber - wrapperFactory.latestIssuedId();
    }

}
