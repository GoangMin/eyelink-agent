package com.m2u.eyelink.context;

import java.util.List;
import java.util.Map;

import com.m2u.eyelink.util.ClassUtils;


public enum HandshakePropertyType {
    SUPPORT_SERVER("supportServer", Boolean.class, false),
    SUPPORT_COMMAND_LIST("supportCommandList", List.class, false),

    HOSTNAME("hostName", String.class),
    IP("ip", String.class),
    AGENT_ID("agentId", String.class),
    APPLICATION_NAME("applicationName", String.class),
    SERVICE_TYPE("serviceType", Integer.class),
    PID("pid", Integer.class),
    VERSION("version", String.class),
    START_TIMESTAMP("startTimestamp", Long.class);


    private final String name;
    private final Class clazzType;
    private final boolean isRequired;

    HandshakePropertyType(String name, Class clazzType) {
        this(name, clazzType, true);
    }

    HandshakePropertyType(String name, Class clazzType, boolean isRequired) {
        this.name = name;
        this.clazzType = clazzType;
        this.isRequired = isRequired;
    }

    public String getName() {
        return name;
    }

    public Class getClazzType() {
        return clazzType;
    }

    public static boolean hasRequiredKeys(Map properties) {
        for (HandshakePropertyType type : HandshakePropertyType.values()) {
            if (!type.isRequired) {
                continue;
            }

            Object value = properties.get(type.getName());

            if (value == null) {
                return false;
            }

            if (!ClassUtils.isAssignable(value.getClass(), type.getClazzType())) {
                return false;
            }
        }

        return true;
    }

}
