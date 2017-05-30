package com.m2u.eyelink.agent.profiler.plugin;

import java.lang.instrument.ClassFileTransformer;

import com.m2u.eyelink.agent.instrument.matcher.Matchable;

public interface MatchableClassFileTransformer extends ClassFileTransformer, Matchable {
    

}
