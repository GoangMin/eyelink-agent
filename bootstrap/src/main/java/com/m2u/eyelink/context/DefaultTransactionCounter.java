package com.m2u.eyelink.context;

import com.m2u.eyelink.agent.profiler.context.TransactionCounter;

public class DefaultTransactionCounter implements TransactionCounter {
    private final IdGenerator idGenerator;

    public DefaultTransactionCounter(IdGenerator idGenerator) {
        if (idGenerator == null) {
            throw new NullPointerException("idGenerator cannot be null");
        }
        this.idGenerator = idGenerator;
    }
    
    @Override
    public long getTransactionCount(SamplingType samplingType) {
        // overflow improbable
        switch (samplingType) {
        case SAMPLED_NEW:
            return idGenerator.currentTransactionId() - IdGenerator.INITIAL_TRANSACTION_ID;
        case SAMPLED_CONTINUATION:
            return Math.abs(idGenerator.currentContinuedTransactionId() - IdGenerator.INITIAL_CONTINUED_TRANSACTION_ID) / IdGenerator.DECREMENT_CYCLE;
        case UNSAMPLED_NEW:
            return Math.abs(idGenerator.currentDisabledId() - IdGenerator.INITIAL_DISABLED_ID) / IdGenerator.DECREMENT_CYCLE;
        case UNSAMPLED_CONTINUATION:
            return Math.abs(idGenerator.currentContinuedDisabledId() - IdGenerator.INITIAL_CONTINUED_DISABLED_ID) / IdGenerator.DECREMENT_CYCLE;
        default:
            return 0L;
        }
    }

    @Override
    public long getTotalTransactionCount() {
        long count = 0L;
        for (SamplingType samplingType : SamplingType.values()) {
            count += getTransactionCount(samplingType);
        }
        return count;
    }
}
