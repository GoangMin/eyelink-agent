package com.m2u.eyelink.agent.profiler.instrument;

import java.io.InputStream;

public interface ClassInjector  {

    <T> Class<? extends T> injectClass(ClassLoader targetClassLoader, String className);

    InputStream getResourceAsStream(ClassLoader targetClassLoader, String classPath);

}