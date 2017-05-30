package com.m2u.eyelink.context;

public class TrueSampler implements Sampler {

    @Override
    public boolean isSampling() {
        return true;
    }

    @Override
    public String toString() {
        // To fix sampler name even if the class name is changed.
        return "TrueSampler";
    }
}
