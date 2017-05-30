package com.m2u.eyelink.agent.profiler.monitor.codahale;

import com.m2u.eyelink.agent.profiler.monitor.AgentStatCollector;
import com.m2u.eyelink.context.thrift.TDataSourceList;


public interface DataSourceCollector extends AgentStatCollector<TDataSourceList> {

    DataSourceCollector EMPTY_DATASOURCE_COLLECTOR = new DataSourceCollector() {

        @Override
        public TDataSourceList collect() {
            return null;
        }

    };

}
