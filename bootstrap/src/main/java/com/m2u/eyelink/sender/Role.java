package com.m2u.eyelink.sender;


public enum Role {
    CALLER, CALLEE, ROUTER, UNKNOWN;

    public static Role getValue(String name) {
        if (name == null) {
            return UNKNOWN;
        }

        for (Role role : Role.values()) {
            if (name.equals(role.name())) {
                return role;
            }
        }

        return UNKNOWN;
    }

}
