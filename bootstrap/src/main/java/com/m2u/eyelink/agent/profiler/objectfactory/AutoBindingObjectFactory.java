package com.m2u.eyelink.agent.profiler.objectfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.plugin.ObjectFactory;
import com.m2u.eyelink.agent.plugin.ObjectFactory.ByConstructor;
import com.m2u.eyelink.agent.plugin.ObjectFactory.ByStaticFactoryMethod;
import com.m2u.eyelink.exception.ELAgentException;

public class AutoBindingObjectFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();
    
    private final InstrumentContext pluginContext;
    private final ClassLoader classLoader;
    private final List<ArgumentProvider> commonProviders;
    
    public AutoBindingObjectFactory(InstrumentContext pluginContext, ClassLoader classLoader, ArgumentProvider... argumentProviders) {
        this.pluginContext = pluginContext;
        this.classLoader = classLoader;
        this.commonProviders = new ArrayList<ArgumentProvider>(Arrays.asList(argumentProviders));
        this.commonProviders.add(new ProfilerPluginArgumentProvider(pluginContext));
    }
    
    public Object createInstance(ObjectFactory objectFactory, ArgumentProvider... providers) {
        final Class<?> type = pluginContext.injectClass(classLoader, objectFactory.getClassName());
        final ArgumentsResolver argumentsResolver = getArgumentResolver(objectFactory, providers);
        
        if (objectFactory instanceof ByConstructor) {
            return byConstructor(type, (ByConstructor) objectFactory, argumentsResolver);
        } else if (objectFactory instanceof ByStaticFactoryMethod) {
            return byStaticFactoryMethod(type, (ByStaticFactoryMethod) objectFactory, argumentsResolver);
        }
        
        throw new IllegalArgumentException("Unknown objectFactory type: " + objectFactory);
    }
    
    private Object byConstructor(Class<?> type, ByConstructor byConstructor, ArgumentsResolver argumentsResolver) {
        final ConstructorResolver resolver = new ConstructorResolver(type, argumentsResolver);
        
        if (!resolver.resolve()) {
            throw new ELAgentException("Cannot find suitable constructor for " + type.getName());
        }
        
        final Constructor<?> constructor = resolver.getResolvedConstructor();
        final Object[] resolvedArguments = resolver.getResolvedArguments();
        
        if (isDebug) {
            logger.debug("Create instance by constructor {}, with arguments {}", constructor, Arrays.toString(resolvedArguments));
        }
        
        try {
            return constructor.newInstance(resolvedArguments);
        } catch (Exception e) {
            throw new ELAgentException("Fail to invoke constructor: " + constructor + ", arguments: " + Arrays.toString(resolvedArguments), e);
        }
    }

    private Object byStaticFactoryMethod(Class<?> type, ByStaticFactoryMethod staticFactoryMethod, ArgumentsResolver argumentsResolver) {
        StaticMethodResolver resolver = new StaticMethodResolver(type, staticFactoryMethod.getFactoryMethodName(), argumentsResolver);
        
        if (!resolver.resolve()) {
            throw new ELAgentException("Cannot find suitable factory method " + type.getName() + "." + staticFactoryMethod.getFactoryMethodName());
        }
        
        final Method method = resolver.getResolvedMethod();
        final Object[] resolvedArguments = resolver.getResolvedArguments();

        if (isDebug) {
            logger.debug("Create instance by static factory method {}, with arguments {}", method, Arrays.toString(resolvedArguments));
        }

        try {
            return method.invoke(null, resolvedArguments);
        } catch (Exception e) {
            throw new ELAgentException("Fail to invoke factory method: " + type.getName() + "." + staticFactoryMethod.getFactoryMethodName() + ", arguments: " + Arrays.toString(resolvedArguments), e);
        }

    }
    
    private ArgumentsResolver getArgumentResolver(ObjectFactory objectFactory, ArgumentProvider[] providers) {
        final List<ArgumentProvider> merged = new ArrayList<ArgumentProvider>(commonProviders);
        merged.addAll(Arrays.asList(providers));
        
        if (objectFactory.getArguments() != null) {
            merged.add(new OrderedValueProvider(this, objectFactory.getArguments()));
        }
        
        return new ArgumentsResolver(merged);
    }
}
