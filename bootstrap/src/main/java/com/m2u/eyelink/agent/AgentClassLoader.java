package com.m2u.eyelink.agent;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Callable;

import com.m2u.eyelink.exception.ELAgentException;

public class AgentClassLoader {


    private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();

    private final URLClassLoader classLoader;

    private String bootClass;

    private final ContextClassLoaderExecuteTemplate<Object> executeTemplate;

    public AgentClassLoader(URL[] urls) {
        if (urls == null) {
            throw new NullPointerException("urls");
        }

        ClassLoader bootStrapClassLoader = AgentClassLoader.class.getClassLoader();
        this.classLoader = createClassLoader(urls, bootStrapClassLoader);

        this.executeTemplate = new ContextClassLoaderExecuteTemplate<Object>(classLoader);
    }

    private ELAgentURLClassLoader createClassLoader(final URL[] urls, final ClassLoader bootStrapClassLoader) {
        if (SECURITY_MANAGER != null) {
        	System.out.println("=====> SECURITY_MANAGER != null");
            return AccessController.doPrivileged(new PrivilegedAction<ELAgentURLClassLoader>() {
                public ELAgentURLClassLoader run() {
                    return new ELAgentURLClassLoader(urls, bootStrapClassLoader);
                }
            });
        } else {
        	System.out.println("=====> SECURITY_MANAGER is null");
            return new ELAgentURLClassLoader(urls, bootStrapClassLoader);
        }
    }

    public void setBootClass(String bootClass) {
        this.bootClass = bootClass;
    }

    public Agent boot(final AgentOption agentOption) {

        final Class<?> bootStrapClazz = getBootStrapClass();

        final Object agent = executeTemplate.execute(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                	System.out.println("=====> AgentClassLoader call() start");
                    Constructor<?> constructor = bootStrapClazz.getConstructor(AgentOption.class);
                    System.out.println("=====> AgentClassLoader call() end");
                    return constructor.newInstance(agentOption);
                } catch (InstantiationException e) {
                    throw new ELAgentException("boot create failed. Error:" + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new ELAgentException("boot method invoke failed. Error:" + e.getMessage(), e);
                }
            }
        });

        if (agent instanceof Agent) {
            return (Agent) agent;
        } else {
            String agentClassName;
            if (agent == null) {
                agentClassName = "Agent is null";
            } else {
                agentClassName = agent.getClass().getName();
            }
            throw new ELAgentException("Invalid AgentType. boot failed. AgentClass:" + agentClassName);
        }
    }

    private Class<?> getBootStrapClass() {
        try {
            return this.classLoader.loadClass(bootClass);
        } catch (ClassNotFoundException e) {
            throw new ELAgentException("boot class not found. bootClass:" + bootClass + " Error:" + e.getMessage(), e);
        }
    }

}
