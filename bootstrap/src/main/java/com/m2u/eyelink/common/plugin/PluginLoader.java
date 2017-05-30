package com.m2u.eyelink.common.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PluginLoader {
    private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();

    public static <T> List<T> load(Class<T> serviceType, URL[] urls) {
        URLClassLoader classLoader = createPluginClassLoader(urls, ClassLoader.getSystemClassLoader());
        return load(serviceType, classLoader);
    }

    private static PluginLoaderClassLoader createPluginClassLoader(final URL[] urls, final ClassLoader parent) {
        if (SECURITY_MANAGER != null) {
            return AccessController.doPrivileged(new PrivilegedAction<PluginLoaderClassLoader>() {
                public PluginLoaderClassLoader run() {
                    return new PluginLoaderClassLoader(urls, parent);
                }
            });
        } else {
        	System.out.println("=====> PluginLoader : SECURITY_MANAGER is null");
            return new PluginLoaderClassLoader(urls, parent);
        }
    }
    
    public static <T> List<T> load(Class<T> serviceType, ClassLoader classLoader) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(serviceType, classLoader);
        
        List<T> plugins = new ArrayList<T>();
        for (T plugin : serviceLoader) {
            plugins.add(serviceType.cast(plugin));
        }

        return plugins;
    }
}
