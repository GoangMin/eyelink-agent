package com.m2u.eyelink.agent;

public class ProfilerLibClass implements LibClass {

	// TODO bootClass가 일치하면 classloading 하지 않음.
    private static final String[] ELAGENT_PROFILER_CLASS = new String[] {
            "com.m2u.eyelink.agent.profiler2",
            "com.m2u.eyelink.context.thrift",
            "com.m2u.eyelink.rpc",
            /*
             * @deprecated javassist
             */
            "javassist",
            "org.objectweb.asm",
            "org.slf4j",
            "org.apache.thrift",
            "org.jboss.netty",
            "com.google.common",
            "org.apache.commons.lang",
            "org.apache.log4j",
            "com.codahale.metrics",
            "com.nhncorp.nelo2"
    };

    @Override
    public boolean onLoadClass(String clazzName) {
        final int length = ELAGENT_PROFILER_CLASS.length;
        for (int i = 0; i < length; i++) {
            if (clazzName.startsWith(ELAGENT_PROFILER_CLASS[i])) {
                return ON_LOAD_CLASS;
            }
        }
        return DELEGATE_PARENT;
    }
}
