package com.m2u.eyelink.agent.profiler.monitor;

import com.m2u.eyelink.context.thrift.TJvmGc;
import com.m2u.eyelink.context.thrift.TJvmGcType;

public class UnknownGarbageCollector implements GarbageCollector {

    public static final TJvmGcType GC_TYPE = TJvmGcType.UNKNOWN;

    @Override
    public int getTypeCode() {
        return GC_TYPE.getValue();
    }

    @Override
    public TJvmGc collect() {
        // return null to prevent data send.
        // (gc field of Thrift DTO is optional)
        return null;
    }

    @Override
    public String toString() {
        return "Unknown Garbage collector";
    }

}
