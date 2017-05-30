package com.m2u.eyelink.context;

import com.m2u.eyelink.annotations.*;

@InterfaceAudience.LimitedPrivate("vert.x")
public interface AsyncStateSupport {
    AsyncState getAsyncState();
}
