package com.m2u.eyelink.agent.profiler;

public class JvmInformation {

    private final String jvmVersion;
    private final int gcTypeCode;

    JvmInformation(String jvmVersion, int gcTypeCode) {
        this.jvmVersion = jvmVersion;
        this.gcTypeCode = gcTypeCode;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public int getGcTypeCode() {
        return gcTypeCode;
    }
}
