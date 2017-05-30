package com.m2u.eyelink.agent.interceptor.scope;


public interface InterceptorScopeInvocation {
    String getName();

    boolean tryEnter(ExecutionPolicy policy);
    boolean canLeave(ExecutionPolicy policy);
    void leave(ExecutionPolicy policy);
    
    boolean isActive();
    
    Object setAttachment(Object attachment);
    Object getAttachment();
    Object getOrCreateAttachment(AttachmentFactory factory);
    Object removeAttachment();

}
