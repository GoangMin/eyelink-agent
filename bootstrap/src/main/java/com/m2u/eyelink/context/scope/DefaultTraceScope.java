package com.m2u.eyelink.context.scope;

import com.m2u.eyelink.context.TraceScope;

public class DefaultTraceScope implements TraceScope {
    private final String name;
    private int depth = 0;

    public DefaultTraceScope(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean tryEnter() {
        // policy is ALWAYS
        depth++;
        return true;
    }

    public boolean canLeave() {
        if(!isActive()) {
            return false;
        }

        return true;
    }

    public void leave() {
        if(!isActive()) {
            throw new IllegalStateException("Cannot leave with scope. depth: " + depth);
        }

        // policy is ALWAYS
        depth--;
    }

    @Override
    public boolean isActive() {
        return depth > 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultTraceScope{");
        sb.append("name='").append(name).append('\'');
        sb.append(", depth=").append(depth);
        sb.append('}');
        return sb.toString();
    }
}