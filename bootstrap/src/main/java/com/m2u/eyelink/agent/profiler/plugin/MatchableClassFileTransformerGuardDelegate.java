package com.m2u.eyelink.agent.profiler.plugin;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.m2u.eyelink.agent.instrument.GuardInstrumentor;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.InstrumentException;
import com.m2u.eyelink.agent.instrument.matcher.Matcher;
import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.exception.ELAgentException;

public class MatchableClassFileTransformerGuardDelegate implements MatchableClassFileTransformer {

    private final InstrumentContext instrumentContext;
    private final Matcher matcher;
    private final TransformCallback transformCallback;


    public MatchableClassFileTransformerGuardDelegate(InstrumentContext instrumentContext, Matcher matcher, TransformCallback transformCallback) {
        if (instrumentContext == null) {
            throw new NullPointerException("instrumentContext must not be null");
        }
        if (matcher == null) {
            throw new NullPointerException("matcher must not be null");
        }
        if (transformCallback == null) {
            throw new NullPointerException("transformCallback must not be null");
        }
        this.instrumentContext = instrumentContext;
        this.matcher = matcher;
        this.transformCallback = transformCallback;
    }


    @Override
    public Matcher getMatcher() {
        return matcher;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MatchableClassFileTransformerGuardDelegate{");
        sb.append("matcher=").append(matcher);
        sb.append(", transformCallback=").append(transformCallback);
        sb.append('}');
        return sb.toString();
    }
}
