package com.m2u.eyelink.agent.profiler.instrument;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.plugin.PluginConfig;
import com.m2u.eyelink.exception.ELAgentException;

public class BootstrapClassLoaderHandler implements ClassInjector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PluginConfig pluginConfig;

    private final Object lock = new Object();
    private boolean injectedToRoot = false;

    public BootstrapClassLoaderHandler(PluginConfig pluginConfig) {
        if (pluginConfig == null) {
            throw new NullPointerException("pluginConfig must not be null");
        }
        this.pluginConfig = pluginConfig;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> injectClass(ClassLoader classLoader, String className) {
        try {
            if (classLoader == null) {
                return (Class<T>) injectClass0(className);
            }
        } catch (Exception e) {
            logger.warn("Failed to load plugin class {} with classLoader {}", className, classLoader, e);
            throw new ELAgentException("Failed to load plugin class " + className + " with classLoader " + classLoader, e);
        }
        throw new ELAgentException("invalid ClassLoader");
    }

    private Class<?> injectClass0(String className) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        appendToBootstrapClassLoaderSearch();
        return Class.forName(className, false, null);
    }

    private void appendToBootstrapClassLoaderSearch() {
        synchronized (lock) {
            if (this.injectedToRoot == false) {
                this.injectedToRoot = true;
                pluginConfig.getInstrumentation().appendToBootstrapClassLoaderSearch(pluginConfig.getPluginJarFile());
                pluginConfig.getClassPool().appendToBootstrapClassPath(pluginConfig.getPluginJarFile().getName());
            }
        }
    }

    @Override
    public InputStream getResourceAsStream(ClassLoader targetClassLoader, String classPath) {
        try {
            if (targetClassLoader == null) {
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                if (classLoader == null) {
                    return null;
                }
                appendToBootstrapClassLoaderSearch();
                return classLoader.getResourceAsStream(classPath);
            }
        } catch (Exception e) {
            logger.warn("Failed to load plugin resource as stream {} with classLoader {}", classPath, targetClassLoader, e);
            return null;
        }
        logger.warn("Invalid bootstrap class loader. cl={}", targetClassLoader);
        return null;
    }
}