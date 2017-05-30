package com.m2u.eyelink.agent.instrument;

public interface InstrumentClassPool {
	InstrumentClass getClass(InstrumentContext instrumentContext,
			ClassLoader classLoader, String classInternalName,
			byte[] classFileBuffer) throws NotFoundInstrumentException;

	boolean hasClass(ClassLoader classLoader, String classBinaryName);

	void appendToBootstrapClassPath(String jar);
}
