package com.m2u.eyelink.agent.instrument;

import java.lang.instrument.ClassFileTransformer;

public interface DynamicTransformTrigger {
    void retransform(Class<?> target, ClassFileTransformer transformer);
    void addClassFileTransformer(ClassLoader classLoader, String targetClassName, ClassFileTransformer transformer);

}
