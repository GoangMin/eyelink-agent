package com.m2u.eyelink.agent.instrument.transformer;

import com.m2u.eyelink.agent.instrument.InstrumentContext;

public class TransformTemplate implements TransformOperations {

    private final InstrumentContext instrumentContext;

    public TransformTemplate(InstrumentContext instrumentContext) {
        if (instrumentContext == null) {
            throw new NullPointerException("instrumentContext must not be null");
        }
        this.instrumentContext = instrumentContext;
    }

    @Override
    public void transform(String className, TransformCallback transformCallback) {
        if (className == null) {
            throw new NullPointerException("className must not be null");
        }
        if (transformCallback == null) {
            throw new NullPointerException("transformCallback must not be null");
        }
        this.instrumentContext.addClassFileTransformer(className, transformCallback);
    }

}
