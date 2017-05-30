package com.m2u.eyelink.context.scope;

public interface PoolObjectFactory<K, V> {
    V create(K key);
}
