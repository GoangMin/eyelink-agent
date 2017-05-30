package com.m2u.eyelink.agent.instrument;

public interface ClassFilter {
    boolean ACCEPT = true;
    boolean REJECT = false;

    boolean accept(InstrumentClass clazz);  
}
