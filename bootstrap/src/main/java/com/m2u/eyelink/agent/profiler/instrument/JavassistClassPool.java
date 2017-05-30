package com.m2u.eyelink.agent.profiler.instrument;

import java.util.List;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentClassPool;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.NotFoundInstrumentException;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistryBinder;

// FIXME not copy & paste from pinpoint
public class JavassistClassPool implements InstrumentClassPool {

	public JavassistClassPool(
			InterceptorRegistryBinder interceptorRegistryBinder,
			List<String> bootstrapJarPaths) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public InstrumentClass getClass(InstrumentContext instrumentContext,
			ClassLoader classLoader, String classInternalName,
			byte[] classFileBuffer) throws NotFoundInstrumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasClass(ClassLoader classLoader, String classBinaryName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void appendToBootstrapClassPath(String jar) {
		// TODO Auto-generated method stub

	}

}
