package com.m2u.eyelink.config;

public enum DumpType {
//  NONE(-1),  comment out because of duplicated configuration.
    ALWAYS(0), EXCEPTION(1);

    private int code;
    DumpType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
