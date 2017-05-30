package com.m2u.eyelink.context;

public interface Binder<T> {

    T get();

    void set(T value);

    T remove();
}
