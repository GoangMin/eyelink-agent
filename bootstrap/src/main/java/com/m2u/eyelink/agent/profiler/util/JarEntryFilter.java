package com.m2u.eyelink.agent.profiler.util;

import java.util.jar.JarEntry;

public interface JarEntryFilter {
	boolean filter(JarEntry jarEntry);
}
