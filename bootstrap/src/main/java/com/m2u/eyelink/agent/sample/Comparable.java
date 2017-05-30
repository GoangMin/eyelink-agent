package com.m2u.eyelink.agent.sample;

public interface Comparable {
	int LESS = -1;
	int EQUAL = 0;
	int GREATER = 1;

	int compareTo(Object o);
}
