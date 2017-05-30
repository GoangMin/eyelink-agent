package com.m2u.eyelink.config;

public interface Filter<T> {
    boolean FILTERED = true;
    boolean NOT_FILTERED = !FILTERED;

    boolean filter(T value);
}
