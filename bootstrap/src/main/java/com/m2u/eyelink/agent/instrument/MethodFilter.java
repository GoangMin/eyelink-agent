package com.m2u.eyelink.agent.instrument;


public interface MethodFilter {
    boolean ACCEPT = true;
    boolean REJECT = false;

    boolean accept(InstrumentMethod method); 
}
