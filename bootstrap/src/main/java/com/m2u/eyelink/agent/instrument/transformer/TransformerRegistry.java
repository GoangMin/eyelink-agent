package com.m2u.eyelink.agent.instrument.transformer;

import java.lang.instrument.ClassFileTransformer;

public interface TransformerRegistry {
	ClassFileTransformer findTransformer(String className);

}
