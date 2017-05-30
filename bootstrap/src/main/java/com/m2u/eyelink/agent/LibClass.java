package com.m2u.eyelink.agent;

public interface LibClass {
    boolean ON_LOAD_CLASS = true;
    boolean DELEGATE_PARENT = false;

    boolean onLoadClass(String clazzName);
}
