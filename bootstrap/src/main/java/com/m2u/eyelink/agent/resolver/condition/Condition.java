package com.m2u.eyelink.agent.resolver.condition;

public interface Condition<T> {
	boolean check(T condition);
}
