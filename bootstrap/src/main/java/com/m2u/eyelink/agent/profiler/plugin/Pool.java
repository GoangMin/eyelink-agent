package com.m2u.eyelink.agent.profiler.plugin;

public interface Pool<K, V> {

    V get(K key);

}
