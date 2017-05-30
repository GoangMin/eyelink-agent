package com.m2u.eyelink.agent.profiler.context.scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.agent.profiler.plugin.Pool;
import com.m2u.eyelink.context.scope.PoolObjectFactory;

public class ConcurrentPool<K, V> implements Pool<K, V> {

    private final ConcurrentMap<K, V> pool = new ConcurrentHashMap<K, V>();

    private final PoolObjectFactory<K, V> objectFactory;

    public ConcurrentPool(PoolObjectFactory<K, V> objectFactory) {
        if (objectFactory == null) {
            throw new NullPointerException("objectFactory must not be null");
        }
        this.objectFactory = objectFactory;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        final V alreadyExist = this.pool.get(key);
        if (alreadyExist != null) {
            return alreadyExist;
        }

        final V newValue = this.objectFactory.create(key);
        final V oldValue = this.pool.putIfAbsent(key, newValue);
        if (oldValue != null) {
            return oldValue;
        }
        return newValue;
    }


}
