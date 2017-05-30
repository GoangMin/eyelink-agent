package com.m2u.eyelink.agent.profiler.monitor.codahale;

import com.m2u.eyelink.agent.profiler.monitor.AgentStatCollector;
import com.m2u.eyelink.context.thrift.TTransaction;

public interface TransactionMetricCollector extends AgentStatCollector<TTransaction> {

    TransactionMetricCollector EMPTY_TRANSACTION_METRIC_COLLECTOR = new TransactionMetricCollector() {
        @Override
        public TTransaction collect() {
            return null;
        }
    };

}
