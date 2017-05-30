package com.m2u.eyelink.agent.profiler.plugin;

import java.util.Arrays;
import java.util.List;

import com.m2u.eyelink.agent.instrument.matcher.Matcher;

public final class Matchers {

    private Matchers() {
    }

    public static Matcher newClassNameMatcher(String classInternalName) {
        return new DefaultClassNameMatcher(classInternalName);
    }

    public static Matcher newMultiClassNameMatcher(List<String> classNameList) {
        if (classNameList == null) {
            throw new NullPointerException("classNameList must not be null");
        }
        return new DefaultMultiClassNameMatcher(classNameList);
    }

    public static Matcher newMultiClassNameMatcher(String... classNameList) {
        if (classNameList == null) {
            throw new NullPointerException("classNameList must not be null");
        }
        return new DefaultMultiClassNameMatcher(Arrays.asList(classNameList));
    }

}
