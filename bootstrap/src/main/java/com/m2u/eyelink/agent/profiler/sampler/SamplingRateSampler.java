package com.m2u.eyelink.agent.profiler.sampler;

import java.util.concurrent.atomic.AtomicInteger;

import com.m2u.eyelink.common.util.MathUtils;
import com.m2u.eyelink.context.Sampler;

public class SamplingRateSampler implements Sampler {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final int samplingRate;

    public SamplingRateSampler(int samplingRate) {
        if (samplingRate <= 0) {
            throw new IllegalArgumentException("Invalid samplingRate " + samplingRate);
        }
        this.samplingRate = samplingRate;
    }



    @Override
    public boolean isSampling() {
        int samplingCount = MathUtils.fastAbs(counter.getAndIncrement());
        int isSampling = samplingCount % samplingRate;
        return isSampling == 0;
    }

    @Override
    public String toString() {
        return "SamplingRateSampler{" +
                    "counter=" + counter +
                    "samplingRate=" + samplingRate +
                '}';
    }
}
