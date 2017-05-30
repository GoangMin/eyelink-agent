package com.m2u.eyelink.agent.profiler.instrument.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentException;
import com.m2u.eyelink.agent.instrument.InstrumentMethod;
import com.m2u.eyelink.agent.instrument.MethodFilters;
import com.m2u.eyelink.agent.interceptor.BasicMethodInterceptor;
import com.m2u.eyelink.agent.profiler.plugin.DefaultProfilerPluginContext;

public class DebugTransformer implements ClassFileTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DefaultProfilerPluginContext context;
    
    public DebugTransformer(DefaultProfilerPluginContext context) {
        this.context = context;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            final InstrumentClass target = context.getInstrumentClass(loader, className, classfileBuffer);
            if (target == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn("targetClass not found. className:{}, classBeingRedefined:{} :{} ", className, classBeingRedefined, loader);
                }
                // throw exception ?
                return null;
            }
            
            if (!target.isInterceptable()) {
                return null;
            }
    
            for (InstrumentMethod method : target.getDeclaredMethods(MethodFilters.ACCEPT_ALL)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("### c={}, m={}, params={}", className, method.getName(), Arrays.toString(method.getParameterTypes()));
                }
                
                method.addInterceptor(BasicMethodInterceptor.class.getName());
            }
    
            return target.toBytecode();
        } catch (InstrumentException e) {
            logger.warn("Failed to instrument " + className, e);
            return null;
        }
    }
    
    
}
