package com.m2u.eyelink.agent.profiler.context;

public interface TransactionCounter {
	   enum SamplingType {
	        SAMPLED_NEW,
	        SAMPLED_CONTINUATION,
	        UNSAMPLED_NEW,
	        UNSAMPLED_CONTINUATION
	    }

	    long getTransactionCount(SamplingType samplingType);

	    long getTotalTransactionCount();

	}
