package com.m2u.eyelink.context.scope;

import com.m2u.eyelink.context.TraceScope;

public class DefaultTraceScopePool {

    private final NameValueList<TraceScope> list = new NameValueList<TraceScope>();

    public TraceScope get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        return list.get(name);
    }

    public TraceScope add(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        final TraceScope oldScope = list.add(name, new DefaultTraceScope(name));
        return oldScope;
    }

    public void clear() {
        list.clear();
    }
}