package com.m2u.eyelink.agent.profiler.monitor.codahale;

import com.m2u.eyelink.agent.profiler.monitor.AgentStatCollector;
import com.m2u.eyelink.context.thrift.TActiveTrace;

public interface ActiveTraceMetricCollector extends AgentStatCollector<TActiveTrace> {

    ActiveTraceMetricCollector EMPTY_ACTIVE_TRACE_COLLECTOR = new ActiveTraceMetricCollector() {
        @Override
        public TActiveTrace collect() {
            return null;
        }
    };

}
