package com.m2u.eyelink.agent.profiler;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.instrument.DynamicTransformRequestListener;
import com.m2u.eyelink.agent.instrument.RequestHandle;
import com.m2u.eyelink.agent.instrument.transformer.TransformerRegistry;
import com.m2u.eyelink.agent.profiler.instrument.LegacyProfilerPluginClassInjector;
import com.m2u.eyelink.agent.profiler.instrument.transformer.DebugTransformer;
import com.m2u.eyelink.agent.profiler.instrument.transformer.DefaultTransformerRegistry;
import com.m2u.eyelink.agent.profiler.plugin.DefaultProfilerPluginContext;
import com.m2u.eyelink.agent.profiler.plugin.MatchableClassFileTransformer;
import com.m2u.eyelink.config.Filter;

public class ClassFileTransformerDispatcher implements ClassFileTransformer,
		DynamicTransformRequestListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean isDebug = logger.isDebugEnabled();

	private final ClassLoader agentClassLoader = this.getClass()
			.getClassLoader();

	private final TransformerRegistry transformerRegistry;
	private final DynamicTransformerRegistry dynamicTransformerRegistry;

	private final DefaultProfilerPluginContext globalContext;
	private final Filter<String> debugTargetFilter;
	private final DebugTransformer debugTransformer;

	private final ClassFileFilter pinpointClassFilter;
	private final ClassFileFilter unmodifiableFilter;

	public ClassFileTransformerDispatcher(DefaultAgent agent,
			List<DefaultProfilerPluginContext> pluginContexts) {
		if (agent == null) {
			throw new NullPointerException("agent must not be null");
		}

		this.globalContext = new DefaultProfilerPluginContext(agent,
				new LegacyProfilerPluginClassInjector(getClass()
						.getClassLoader()));
		this.debugTargetFilter = agent.getProfilerConfig()
				.getProfilableClassFilter();
		this.debugTransformer = new DebugTransformer(globalContext);

		this.pinpointClassFilter = new ELAgentClassFilter(agentClassLoader);
		this.unmodifiableFilter = new UnmodifiableClassFilter();

		this.transformerRegistry = createTransformerRegistry(pluginContexts);
		this.dynamicTransformerRegistry = new DefaultDynamicTransformerRegistry();
	}

	@Override
	public byte[] transform(ClassLoader classLoader, String classInternalName,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classFileBuffer) throws IllegalClassFormatException {
		if (!pinpointClassFilter.accept(classLoader, classInternalName,
				classBeingRedefined, protectionDomain, classFileBuffer)) {
			return null;
		}

		final ClassFileTransformer dynamicTransformer = dynamicTransformerRegistry
				.getTransformer(classLoader, classInternalName);
		if (dynamicTransformer != null) {
			return transform0(classLoader, classInternalName,
					classBeingRedefined, protectionDomain, classFileBuffer,
					dynamicTransformer);
		}

		if (!unmodifiableFilter.accept(classLoader, classInternalName,
				classBeingRedefined, protectionDomain, classFileBuffer)) {
			return null;
		}

		ClassFileTransformer transformer = this.transformerRegistry
				.findTransformer(classInternalName);
		if (transformer == null) {
			// For debug
			// TODO What if a modifier is duplicated?
			if (this.debugTargetFilter.filter(classInternalName)) {
				// Added to see if call stack view is OK on a test machine.
				transformer = debugTransformer;
			} else {
				return null;
			}
		}

		return transform0(classLoader, classInternalName, classBeingRedefined,
				protectionDomain, classFileBuffer, transformer);
	}

	private byte[] transform0(ClassLoader classLoader,
			String classInternalName, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classFileBuffer,
			ClassFileTransformer transformer) {
		final String className = JavaAssistUtils
				.jvmNameToJavaName(classInternalName);

		if (isDebug) {
			if (classBeingRedefined == null) {
				logger.debug(
						"[transform] classLoader:{} className:{} transformer:{}",
						classLoader, className, transformer.getClass()
								.getName());
			} else {
				logger.debug(
						"[retransform] classLoader:{} className:{} transformer:{}",
						classLoader, className, transformer.getClass()
								.getName());
			}
		}

		try {
			final Thread thread = Thread.currentThread();
			final ClassLoader before = getContextClassLoader(thread);
			thread.setContextClassLoader(this.agentClassLoader);
			try {
				return transformer.transform(classLoader, className,
						classBeingRedefined, protectionDomain, classFileBuffer);
			} finally {
				// The context class loader have to be recovered even if it was
				// null.
				thread.setContextClassLoader(before);
			}
		} catch (Throwable e) {
			logger.error(
					"Transformer:{} threw an exception. cl:{} ctxCl:{} agentCl:{} Cause:{}",
					transformer.getClass().getName(), classLoader, Thread
							.currentThread().getContextClassLoader(),
					agentClassLoader, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public RequestHandle onRetransformRequest(Class<?> target,
			final ClassFileTransformer transformer) {
		return this.dynamicTransformerRegistry.onRetransformRequest(target,
				transformer);
	}

	@Override
	public void onTransformRequest(ClassLoader classLoader,
			String targetClassName, ClassFileTransformer transformer) {
		this.dynamicTransformerRegistry.onTransformRequest(classLoader,
				targetClassName, transformer);
	}

	private ClassLoader getContextClassLoader(Thread thread) throws Throwable {
		try {
			return thread.getContextClassLoader();
		} catch (SecurityException se) {
			throw se;
		} catch (Throwable th) {
			if (isDebug) {
				logger.debug("getContextClassLoader(). Caused:{}",
						th.getMessage(), th);
			}
			throw th;
		}
	}

	private TransformerRegistry createTransformerRegistry(
			List<DefaultProfilerPluginContext> pluginContexts) {
		DefaultTransformerRegistry registry = new DefaultTransformerRegistry();

		for (DefaultProfilerPluginContext pluginContext : pluginContexts) {
			for (ClassFileTransformer transformer : pluginContext
					.getClassEditors()) {
				if (transformer instanceof MatchableClassFileTransformer) {
					MatchableClassFileTransformer t = (MatchableClassFileTransformer) transformer;
					logger.info(
							"Registering class file transformer {} for {} ", t,
							t.getMatcher());
					registry.addTransformer(t.getMatcher(), t);
				} else {
					logger.warn("Ignore class file transformer {}", transformer);
				}
			}
		}

		return registry;
	}
}