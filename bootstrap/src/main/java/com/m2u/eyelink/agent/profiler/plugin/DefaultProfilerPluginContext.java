package com.m2u.eyelink.agent.profiler.plugin;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.util.ArrayList;
import java.util.List;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentClassPool;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.NotFoundInstrumentException;
import com.m2u.eyelink.agent.instrument.matcher.Matcher;
import com.m2u.eyelink.agent.instrument.transformer.TransformCallback;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScopeFactory;
import com.m2u.eyelink.agent.profiler.DefaultAgent;
import com.m2u.eyelink.agent.profiler.DynamicTransformService;
import com.m2u.eyelink.agent.profiler.JavaAssistUtils;
import com.m2u.eyelink.agent.profiler.context.scope.ConcurrentPool;
import com.m2u.eyelink.agent.profiler.instrument.ClassInjector;
import com.m2u.eyelink.agent.profiler.instrument.PluginClassInjector;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.TraceContext;

public class DefaultProfilerPluginContext implements
		ProfilerPluginSetupContext, InstrumentContext {
	private final DefaultAgent agent;
	private final ClassInjector classInjector;

	private final List<ApplicationTypeDetector> serverTypeDetectors = new ArrayList<ApplicationTypeDetector>();
	private final List<ClassFileTransformer> classTransformers = new ArrayList<ClassFileTransformer>();

	private final Pool<String, InterceptorScope> interceptorScopePool = new ConcurrentPool<String, InterceptorScope>(
			new InterceptorScopeFactory());

	public DefaultProfilerPluginContext(DefaultAgent agent,
			ClassInjector classInjector) {
		if (agent == null) {
			throw new NullPointerException("agent must not be null");
		}
		if (classInjector == null) {
			throw new NullPointerException("classInjector must not be null");
		}
		this.agent = agent;
		this.classInjector = classInjector;
	}

	@Override
	public ProfilerConfig getConfig() {
		return agent.getProfilerConfig();
	}

	public PluginConfig getPluginConfig() {
		if (classInjector instanceof PluginClassInjector) {
			return ((PluginClassInjector) classInjector).getPluginConfig();
		}
		return null;
	}

	@Override
	public TraceContext getTraceContext() {
		final TraceContext context = agent.getTraceContext();
		if (context == null) {
			throw new IllegalStateException("TraceContext is not created yet");
		}

		return context;
	}

	@Override
	public void addApplicationTypeDetector(ApplicationTypeDetector... detectors) {
		if (detectors == null) {
			return;
		}
		for (ApplicationTypeDetector detector : detectors) {
			serverTypeDetectors.add(detector);
		}
	}

	@Override
	public InstrumentClass getInstrumentClass(ClassLoader classLoader,
			String className, byte[] classFileBuffer) {
		if (className == null) {
			throw new NullPointerException("className must not be null");
		}
		try {
			final InstrumentClassPool classPool = getClassPool();
			return classPool.getClass(this, classLoader, className,
					classFileBuffer);
		} catch (NotFoundInstrumentException e) {
			return null;
		}
	}

	@Override
	public boolean exist(ClassLoader classLoader, String className) {
		if (className == null) {
			throw new NullPointerException("className must not be null");
		}
		final InstrumentClassPool classPool = getClassPool();
		return classPool.hasClass(classLoader, className);
	}

	private InstrumentClassPool getClassPool() {
		InstrumentClassPool classPool = agent.getClassPool();
		return classPool;
	}

	@Override
	public void addClassFileTransformer(final String targetClassName,
			final TransformCallback transformCallback) {
		if (targetClassName == null) {
			throw new NullPointerException("targetClassName must not be null");
		}
		if (transformCallback == null) {
			throw new NullPointerException("transformCallback must not be null");
		}

		final Matcher matcher = Matchers.newClassNameMatcher(JavaAssistUtils
				.javaNameToJvmName(targetClassName));
		final MatchableClassFileTransformerGuardDelegate guard = new MatchableClassFileTransformerGuardDelegate(
				this, matcher, transformCallback);
		classTransformers.add(guard);
	}

	@Override
	public void addClassFileTransformer(ClassLoader classLoader,
			String targetClassName, final TransformCallback transformCallback) {
		if (targetClassName == null) {
			throw new NullPointerException("targetClassName must not be null");
		}
		if (transformCallback == null) {
			throw new NullPointerException("transformCallback must not be null");
		}

		final ClassFileTransformerGuardDelegate classFileTransformerGuardDelegate = new ClassFileTransformerGuardDelegate(
				this, transformCallback);

		final DynamicTransformService dynamicTransformService = agent
				.getDynamicTransformService();
		dynamicTransformService.addClassFileTransformer(classLoader,
				targetClassName, classFileTransformerGuardDelegate);
	}

	@Override
	public void retransform(Class<?> target,
			final TransformCallback transformCallback) {
		if (target == null) {
			throw new NullPointerException("target must not be null");
		}
		if (transformCallback == null) {
			throw new NullPointerException("transformCallback must not be null");
		}

		final ClassFileTransformerGuardDelegate classFileTransformerGuardDelegate = new ClassFileTransformerGuardDelegate(
				this, transformCallback);

		final DynamicTransformService dynamicTransformService = agent
				.getDynamicTransformService();
		dynamicTransformService.retransform(target,
				classFileTransformerGuardDelegate);
	}

	@Override
	public <T> Class<? extends T> injectClass(ClassLoader targetClassLoader,
			String className) {
		if (className == null) {
			throw new NullPointerException("className must not be null");
		}

		return classInjector.injectClass(targetClassLoader, className);
	}

	@Override
	public InputStream getResourceAsStream(ClassLoader targetClassLoader,
			String classPath) {
		if (classPath == null) {
			return null;
		}

		return classInjector.getResourceAsStream(targetClassLoader, classPath);
	}

	public List<ClassFileTransformer> getClassEditors() {
		return classTransformers;
	}

	public List<ApplicationTypeDetector> getApplicationTypeDetectors() {
		return serverTypeDetectors;
	}

	@Override
	public InterceptorScope getInterceptorScope(String name) {
		if (name == null) {
			throw new NullPointerException("name must not be null");
		}

		return interceptorScopePool.get(name);
	}
}
