package com.m2u.eyelink.agent.interceptor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TargetFilter {
    /**
     * Filter type
     */
    String type();
    
    /**
     * Arguments for specified {@link MethodFilter}'s constructor. 
     */
    String[] constructorArguments() default {};
    
    boolean singleton() default false;
}
