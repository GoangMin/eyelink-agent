package com.m2u.eyelink.agent.profiler.instrument.classreading;

import java.util.List;

public interface SimpleClassMetadata {

    int getVersion();

    int getAccessFlag();

    String getClassName();

    String getSuperClassName();

    List<String> getInterfaceNames();

    byte[] getClassBinary();

    void setDefinedClass(Class<?> definedClass);

    Class<?> getDefinedClass();
}
