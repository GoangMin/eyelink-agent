package com.m2u.eyelink.agent.profiler.objectfactory;


public interface JudgingParameterResolver extends ArgumentProvider {
    void prepare();
    boolean isAcceptable();
}