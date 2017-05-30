package com.m2u.eyelink.agent.profiler.sampler;

import com.m2u.eyelink.context.Sampler;

public class FalseSampler implements Sampler {
    @Override
    public boolean isSampling() {
        return false;
    }

    @Override
    public String toString() {
        // To fix sampler name even if the class name is changed.
        return "FalseSampler";
    }
}
