package com.m2u.eyelink.agent.profiler.sampler;

import com.m2u.eyelink.context.Sampler;
import com.m2u.eyelink.context.TrueSampler;

public class SamplerFactory {
    public Sampler createSampler(boolean sampling, int samplingRate) {
        if (!sampling || samplingRate <= 0) {
            return new FalseSampler();
        }
        if (samplingRate == 1) {
            return new TrueSampler();
        }
        return new SamplingRateSampler(samplingRate);
    }
}
