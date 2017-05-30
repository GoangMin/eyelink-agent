package com.m2u.eyelink.trace;

public class DefaultAnnotationKeyFactory extends AnnotationKeyFactory {
    public AnnotationKey createAnnotationKey(int code, String name, AnnotationKeyProperty... properties) {
        return new DefaultAnnotationKey(code, name, properties);
    }

}
