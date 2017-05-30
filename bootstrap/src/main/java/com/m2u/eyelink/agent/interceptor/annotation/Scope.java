package com.m2u.eyelink.agent.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.m2u.eyelink.agent.interceptor.scope.ExecutionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {
    /**
     * scope name
     */
    String value();
    
    /**
     * specify when this interceptor have to be invoked.
     */
    ExecutionPolicy executionPolicy() default ExecutionPolicy.BOUNDARY;
}

