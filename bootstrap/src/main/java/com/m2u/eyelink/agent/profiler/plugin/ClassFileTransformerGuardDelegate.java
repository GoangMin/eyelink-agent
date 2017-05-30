package com.m2u.eyelink.agent.profiler.plugin;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.m2u.eyelink.agent.instrument.GuardInstrumentor;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.InstrumentException;
import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.exception.ELAgentException;

public class ClassFileTransformerGuardDelegate implements ClassFileTransformer {
    private final InstrumentContext instrumentContext;
    private final TransformCallback transformCallback;

    public ClassFileTransformerGuardDelegate(InstrumentContext instrumentContext, TransformCallback transformCallback) {
        if (instrumentContext == null) {
            throw new NullPointerException("instrumentContext must not be null");
        }
        if (transformCallback == null) {
            throw new NullPointerException("transformCallback must not be null");
        }
        this.instrumentContext = instrumentContext;
        this.transformCallback = transformCallback;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            throw new NullPointerException("className must not be null");
        }

        final GuardInstrumentor guard = new GuardInstrumentor(this.instrumentContext);
        try {
            // WARN external plugin api
            return transformCallback.doInTransform(guard, loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        } catch (InstrumentException e) {
            throw new ELAgentException(e);
        } finally {
            guard.close();
        }
    }
}
