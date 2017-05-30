package com.m2u.eyelink.context;

import com.m2u.eyelink.annotations.InterfaceAudience;

@InterfaceAudience.LimitedPrivate("vert.x")
public interface AsyncState {

    void setup();

    boolean await();

    void finish();
}