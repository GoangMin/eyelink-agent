package com.m2u.eyelink.agent;

import java.util.concurrent.Callable;

import com.m2u.eyelink.exception.ELAgentException;

public class ContextClassLoaderExecuteTemplate<V> {
    private final ClassLoader classLoader;

    public ContextClassLoaderExecuteTemplate(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new NullPointerException("classLoader must not be null");
        }
        this.classLoader = classLoader;
    }

    public V execute(Callable<V> callable) throws ELAgentException {
        try {
            final Thread currentThread = Thread.currentThread();
            final ClassLoader before = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(ContextClassLoaderExecuteTemplate.this.classLoader);
            try {
            	System.out.println("=====> ContextClassLoaderExecuteTemplate call()");
                return callable.call();
            } finally {
                // even though  the "BEFORE" classloader  is null, rollback  is needed.
                // if an exception occurs BEFORE callable.call(), the call flow can't reach here.
                // so  rollback  here is right.
                currentThread.setContextClassLoader(before);
            }
        } catch (ELAgentException ex){
            throw ex;
        } catch (Exception ex) {
            throw new ELAgentException("execute fail. Error:" + ex.getMessage(), ex);
        }
    }
}
