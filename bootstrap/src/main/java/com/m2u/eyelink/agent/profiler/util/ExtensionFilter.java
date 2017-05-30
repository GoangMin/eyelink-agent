package com.m2u.eyelink.agent.profiler.util;

import java.util.jar.JarEntry;

public class ExtensionFilter implements JarEntryFilter {

    public static final JarEntryFilter CLASS_FILTER = new ExtensionFilter(".class");

    private final String extension;

    public ExtensionFilter(String extension) {
        if (extension == null) {
            throw new NullPointerException("extension must not be null");
        }
        this.extension = extension;
    }

    @Override
    public boolean filter(JarEntry jarEntry) {
        final String name = jarEntry.getName();
        return name.endsWith(extension);
    }
}
