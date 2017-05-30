package com.m2u.eyelink.config;

public class SkipFilter<T> implements Filter<T> {

    @Override
    public boolean filter(T value) {
        return false;
    }

}
