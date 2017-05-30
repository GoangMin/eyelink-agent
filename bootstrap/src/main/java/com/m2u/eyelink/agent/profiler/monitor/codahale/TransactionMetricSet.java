package com.m2u.eyelink.agent.profiler.monitor.codahale;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.m2u.eyelink.agent.profiler.context.TransactionCounter;
import com.m2u.eyelink.agent.profiler.context.TransactionCounter.SamplingType;


public class TransactionMetricSet implements MetricSet {

    private final Gauge<Long> sampledNewGauge;
    private final Gauge<Long> sampledContinuationGauge;
    private final Gauge<Long> unsampledNewGauge;
    private final Gauge<Long> unsampledContinuationGuage;

    public TransactionMetricSet(TransactionCounter transactionCounter) {
        if (transactionCounter == null) {
            throw new NullPointerException("transactionCounter must not be null");
        }
        this.sampledNewGauge = new TransactionGauge(transactionCounter, SamplingType.SAMPLED_NEW);
        this.sampledContinuationGauge = new TransactionGauge(transactionCounter, SamplingType.SAMPLED_CONTINUATION);
        this.unsampledNewGauge = new TransactionGauge(transactionCounter, SamplingType.UNSAMPLED_NEW);
        this.unsampledContinuationGuage = new TransactionGauge(transactionCounter, SamplingType.UNSAMPLED_CONTINUATION);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<String, Metric>();
        gauges.put(MetricMonitorValues.TRANSACTION_SAMPLED_NEW, this.sampledNewGauge);
        gauges.put(MetricMonitorValues.TRANSACTION_SAMPLED_CONTINUATION, this.sampledContinuationGauge);
        gauges.put(MetricMonitorValues.TRANSACTION_UNSAMPLED_NEW, this.unsampledNewGauge);
        gauges.put(MetricMonitorValues.TRANSACTION_UNSAMPLED_CONTINUATION, this.unsampledContinuationGuage);
        return Collections.unmodifiableMap(gauges);
    }

    @Override
    public String toString() {
        return "Default TransactionMetricSet";
    }

    private static class TransactionGauge implements Gauge<Long> {
        private static final long UNINITIALIZED = -1L;

        private final TransactionCounter transactionCounter;
        private final SamplingType samplingType;

        private long prevTransactionCount = UNINITIALIZED;

        private TransactionGauge(TransactionCounter transactionCounter, SamplingType samplingType) {
            this.transactionCounter = transactionCounter;
            this.samplingType = samplingType;
        }

        @Override
        public final Long getValue() {
            final long transactionCount = this.transactionCounter.getTransactionCount(this.samplingType);
            if (transactionCount < 0) {
                return 0L;
            }
            if (this.prevTransactionCount == UNINITIALIZED) {
                this.prevTransactionCount = transactionCount;
                return 0L;
            }
            final long transactionCountDelta = transactionCount - this.prevTransactionCount;
            this.prevTransactionCount = transactionCount;
            return transactionCountDelta;
        }
    }

}
