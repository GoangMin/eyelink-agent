package com.m2u.eyelink.context.thrift;

final class HeaderUtils {
    public static final int OK = Header.SIGNATURE;
    public static final int FAIL = 0;

    private HeaderUtils() {
    }

    public static int validateSignature(byte signature) {
        if (Header.SIGNATURE == signature) {
            return OK;
        } 
        return FAIL;
    }
}
