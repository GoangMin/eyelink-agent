package com.m2u.eyelink.context;

public interface RemoteAddressResolver<T> {
    String resolve(T target);
}
