package com.m2u.eyelink.agent.instrument.transformer;

import java.security.ProtectionDomain;

import com.m2u.eyelink.agent.instrument.InstrumentException;
import com.m2u.eyelink.agent.instrument.Instrumentor;

@Plugin
public interface TransformCallback {

	byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader,
			String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws InstrumentException;

}
