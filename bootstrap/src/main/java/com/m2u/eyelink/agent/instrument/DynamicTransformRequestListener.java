package com.m2u.eyelink.agent.instrument;

import java.lang.instrument.ClassFileTransformer;


public interface DynamicTransformRequestListener {
    RequestHandle onRetransformRequest(Class<?> target, ClassFileTransformer transformer);

    void onTransformRequest(ClassLoader classLoader, String targetClassName, ClassFileTransformer transformer);

}
