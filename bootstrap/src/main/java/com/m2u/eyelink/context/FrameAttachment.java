package com.m2u.eyelink.context;

public interface FrameAttachment {

    Object attachFrameObject(Object frameObject);

    Object getFrameObject();

    Object detachFrameObject();
}
