package com.m2u.eyelink.agent.profiler.monitor.codahale;

import static com.m2u.eyelink.agent.profiler.monitor.codahale.MetricMonitorValues.*;

import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.m2u.eyelink.context.thrift.TTransaction;

public class DefaultTransactionMetricCollector implements TransactionMetricCollector {

    private static final long UNSUPPORTED_TRANSACTION_METRIC = -1;
    private static final Gauge<Long> UNSUPPORTED_GAUGE = new EmptyGauge<Long>(UNSUPPORTED_TRANSACTION_METRIC);

    private final Gauge<Long> sampledNewGauge;
    private final Gauge<Long> sampledContinuationGauge;
    private final Gauge<Long> unsampledNewGauge;
    private final Gauge<Long> unsampledContinuationGuage;

    @SuppressWarnings("unchecked")
    public DefaultTransactionMetricCollector(TransactionMetricSet transactionMetricSet) {
        if (transactionMetricSet == null) {
            throw new NullPointerException("transactionMetricSet must not be null");
        }
        Map<String, Metric> metrics = transactionMetricSet.getMetrics();
        this.sampledNewGauge = (Gauge<Long>) MetricMonitorValues.getMetric(metrics, TRANSACTION_SAMPLED_NEW, UNSUPPORTED_GAUGE);
        this.sampledContinuationGauge = (Gauge<Long>) MetricMonitorValues.getMetric(metrics, TRANSACTION_SAMPLED_CONTINUATION, UNSUPPORTED_GAUGE);
        this.unsampledNewGauge = (Gauge<Long>) MetricMonitorValues.getMetric(metrics, TRANSACTION_UNSAMPLED_NEW, UNSUPPORTED_GAUGE);
        this.unsampledContinuationGuage = (Gauge<Long>) MetricMonitorValues.getMetric(metrics, TRANSACTION_UNSAMPLED_CONTINUATION, UNSUPPORTED_GAUGE);
    }

    @Override
    public TTransaction collect() {
        TTransaction transaction = new TTransaction();
        transaction.setSampledNewCount(this.sampledNewGauge.getValue());
        transaction.setSampledContinuationCount(this.sampledContinuationGauge.getValue());
        transaction.setUnsampledNewCount(this.unsampledNewGauge.getValue());
        transaction.setUnsampledContinuationCount(this.unsampledContinuationGuage.getValue());
        return transaction;
    }
}
