package com.m2u.eyelink.agent.instrument.matcher;

import java.util.List;

public interface MultiClassNameMatcher extends ClassMatcher {
    List<String> getClassNames();
}
