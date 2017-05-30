package com.m2u.eyelink.agent;

import java.net.URL;
import java.net.URLClassLoader;

public class ELAgentURLClassLoader extends URLClassLoader {

    private static final LibClass PROFILER_LIB_CLASS = new ProfilerLibClass();

    private final ClassLoader parent;

    private final LibClass libClass;

    public ELAgentURLClassLoader(URL[] urls, ClassLoader parent, LibClass libClass) {
        super(urls, parent);
        if (parent == null) {
            throw new NullPointerException("parent must not be null");
        }
        if (libClass == null) {
            throw new NullPointerException("libClass must not be null");
        }
        this.parent = parent;
        this.libClass = libClass;
    }

    public ELAgentURLClassLoader(URL[] urls, ClassLoader parent) {
        this(urls, parent, PROFILER_LIB_CLASS);
    }


    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    	System.out.println("loadClass start");
        // First, check if the class has already been loaded
        Class clazz = findLoadedClass(name);
        if (clazz == null) {
        	System.out.println("=====> name : " + name + ", " + onLoadClass(name));
            if (onLoadClass(name)) {
                // load a class used for Pinpoint itself by this PinpointURLClassLoader
                clazz = findClass(name);
            } else {
                try {
                    // load a class by parent ClassLoader
                    clazz = parent.loadClass(name);
                } catch (ClassNotFoundException ignore) {
                }
                if (clazz == null) {
                    // if not found, try to load a class by this PinpointURLClassLoader
                    clazz = findClass(name);
                }
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
    	System.out.println("loadClass end");
    	return clazz;
    }

    // for test
    boolean onLoadClass(String name) {
        return libClass.onLoadClass(name);
    }

}
