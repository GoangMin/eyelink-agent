package com.m2u.eyelink.agent.profiler.instrument;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentClassPool;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.NotFoundInstrumentException;
import com.m2u.eyelink.agent.profiler.JavaAssistUtils;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistryBinder;

public class ASMClassPool implements InstrumentClassPool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean isInfo = logger.isInfoEnabled();
    private final boolean isDebug = logger.isDebugEnabled();

    private final InterceptorRegistryBinder interceptorRegistryBinder;

    public ASMClassPool(final InterceptorRegistryBinder interceptorRegistryBinder, final List<String> bootStrapJars) {
        if (interceptorRegistryBinder == null) {
            throw new NullPointerException("interceptorRegistryBinder must not be null");
        }

        this.interceptorRegistryBinder = interceptorRegistryBinder;
    }

    @Override
    public InstrumentClass getClass(InstrumentContext instrumentContext, ClassLoader classLoader, String className, byte[] classFileBuffer) throws NotFoundInstrumentException {
        if (className == null) {
            throw new NullPointerException("class name must not be null.");
        }

        try {
            if (classFileBuffer == null) {
                ASMClassNodeAdapter classNode = ASMClassNodeAdapter.get(instrumentContext, classLoader, JavaAssistUtils.javaNameToJvmName(className));
                if (classNode == null) {
                    return null;
                }

                return new ASMClass(instrumentContext, interceptorRegistryBinder, classLoader, classNode);
            }

            // Use ASM tree api.
            final ClassReader classReader = new ClassReader(classFileBuffer);
            final ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            return new ASMClass(instrumentContext, interceptorRegistryBinder, classLoader, classNode);
        } catch (Exception e) {
            throw new NotFoundInstrumentException(e);
        }
    }

    @Override
    public boolean hasClass(ClassLoader classLoader, String className) {
        // TODO deprecated
        return classLoader.getResource(JavaAssistUtils.javaNameToJvmName(className) + ".class") != null;
    }

    @Override
    public void appendToBootstrapClassPath(String jar) {
        // nothing.
    }
}