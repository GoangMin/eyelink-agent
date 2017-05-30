package com.m2u.eyelink.agent.profiler.objectfactory;

import java.lang.annotation.Annotation;


public interface ArgumentProvider {
    Option get(int index, Class<?> type, Annotation[] annotations);
}
