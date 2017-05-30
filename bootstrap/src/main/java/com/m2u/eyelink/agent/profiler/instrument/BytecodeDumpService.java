package com.m2u.eyelink.agent.profiler.instrument;

public interface BytecodeDumpService {
    void dumpBytecode(String dumpMessage, String jvmClassName, byte[] bytes, ClassLoader classLoader);

}
