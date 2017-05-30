package com.m2u.eyelink.agent;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.m2u.eyelink.common.ELConstants;
import com.m2u.eyelink.common.Version;
import com.m2u.eyelink.common.service.AnnotationKeyRegistryService;
import com.m2u.eyelink.common.service.DefaultAnnotationKeyRegistryService;
import com.m2u.eyelink.common.service.DefaultServiceTypeRegistryService;
import com.m2u.eyelink.common.service.DefaultTraceMetadataLoaderService;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.common.service.TraceMetadataLoaderService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.logging.CommonLoggerFactory;
import com.m2u.eyelink.logging.ELLogger;
import com.m2u.eyelink.logging.StdoutCommonLoggerFactory;
import com.m2u.eyelink.plugin.tomcat.DefaultProfilerConfig;
import com.m2u.eyelink.util.ELAgentThreadFactory;
import com.m2u.eyelink.util.SimpleProperty;
import com.m2u.eyelink.util.SystemProperty;

public class ELAgentStarter {

	private final ELLogger logger = ELLogger.getLogger(this.getClass()
			.getName());

	public static final String AGENT_TYPE = "AGENT_TYPE";
	public static final String DEFAULT_AGENT = "DEFAULT_AGENT";
	public static final String BOOT_CLASS = "com.m2u.eyelink.agent.profiler.DefaultAgent";

	public static final String PLUGIN_TEST_AGENT = "PLUGIN_TEST";
	public static final String PLUGIN_TEST_BOOT_CLASS = "com.m2u.eyelink.agent.test.PluginTestAgent";

	private SimpleProperty systemProperty = SystemProperty.INSTANCE;
	
	private final Map<String, String> agentArgs;
	private final ELAgentJarFile agentJarFile;
	private final ClassPathResolver classPathResolver;
	private final Instrumentation instrumentation;

	public ELAgentStarter(Map<String, String> agentArgsMap,
			ELAgentJarFile agentJarFile, ClassPathResolver classPathResolver,
			Instrumentation inst) {
		if (agentArgsMap == null) {
			throw new NullPointerException("agentArgsMap must not be null");
		}
		if (agentJarFile == null) {
			throw new NullPointerException("ELAgentJarFile must not be null");
		}
		if (classPathResolver == null) {
			throw new NullPointerException("classPathResolver must not be null");
		}
		if (inst == null) {
			throw new NullPointerException("instrumentation must not be null");
		}
		this.agentArgs = agentArgsMap;
		this.agentJarFile = agentJarFile;
		this.classPathResolver = classPathResolver;
		this.instrumentation = inst;

	}

	boolean start() {
		logger.info("=============> start()");

		// check eyelink.agentId, eyelink.applicationName setting in arguement
		final IdValidator idValidator = new IdValidator();
		final String agentId = idValidator.getAgentId();
		if (agentId == null) {
			return false;
		}
		final String applicationName = idValidator.getApplicationName();
		if (applicationName == null) {
			return false;
		}

		// loading Plugin jar files
		URL[] pluginJars = classPathResolver.resolvePlugins();

		logger.info("=====> pluginJars size : "+ pluginJars.length);
		
		// TODO using PLogger instead of CommonLogger
		CommonLoggerFactory loggerFactory = StdoutCommonLoggerFactory.INSTANCE;
		TraceMetadataLoaderService typeLoaderService = new DefaultTraceMetadataLoaderService(
				pluginJars, loggerFactory);
		ServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService(
				typeLoaderService, loggerFactory);
		AnnotationKeyRegistryService annotationKeyRegistryService = new DefaultAnnotationKeyRegistryService(
				typeLoaderService, loggerFactory);

		String configPath = getConfigPath(classPathResolver);
		if (configPath == null) {
			return false;
		}
		
		logger.info("=============> saveLogFilePath");

		// TODO skip for next time
		// set the path of log file as a system property
		saveLogFilePath(classPathResolver);

		logger.info("=============> saveELAgentVersion");
		saveELAgentVersion();

		logger.info("=============> try ProfilerConfig");
		try {

			// Is it right to load the configuration in the bootstrap?
			ProfilerConfig profilerConfig = DefaultProfilerConfig
					.load(configPath);
			logger.info("=============> libUrlList");

			// this is the library list that must be loaded
			List<URL> libUrlList = resolveLib(classPathResolver);
			AgentClassLoader agentClassLoader = new AgentClassLoader(
					libUrlList.toArray(new URL[libUrlList.size()]));
			final String bootClass = getBootClass();
			agentClassLoader.setBootClass(bootClass);
			logger.info("EyeLink agent [" + bootClass + "] starting...");
			
//			logger.info("=====> size : " + libUrlList.size());
//			ClassLoader cl = new URLClassLoader(new URL[libUrlList.size()]);
//			Class cls = cl.loadClass("org.jboss.netty.channel.group.ChannelGroup");
			
			
			AgentOption option = createAgentOption(agentId, applicationName,
					profilerConfig, instrumentation, pluginJars,
					agentJarFile, serviceTypeRegistryService,
					annotationKeyRegistryService);
//			logger.info("=====> AgentOption : " + option.toString());
			Agent elAgent = agentClassLoader.boot(option);
			elAgent.start();
			registerShutdownHook(elAgent);

			logger.info("EyeLink agent started normally.");

		} catch (Exception e) {
			logger.warn(ELConstants.ProductName + " start failed.", e);
			return false;
		}
		return true;
	}

	private String getBootClass() {
		final String agentType = getAgentType().toUpperCase();
		if (PLUGIN_TEST_AGENT.equals(agentType)) {
			return PLUGIN_TEST_BOOT_CLASS;
		}
		return BOOT_CLASS;
	}

	private AgentOption createAgentOption(String agentId,
			String applicationName, ProfilerConfig profilerConfig,
			Instrumentation instrumentation, URL[] pluginJars,
			ELAgentJarFile bootstrapJarFile,
			ServiceTypeRegistryService serviceTypeRegistryService,
			AnnotationKeyRegistryService annotationKeyRegistryService) {
		List<String> bootstrapJarPaths = bootstrapJarFile.getJarNameList();
		return new DefaultAgentOption(instrumentation, agentId,
				applicationName, profilerConfig, pluginJars, bootstrapJarPaths,
				serviceTypeRegistryService, annotationKeyRegistryService);
	}

	// for test
	void setSystemProperty(SimpleProperty systemProperty) {
		this.systemProperty = systemProperty;
	}

	private void registerShutdownHook(final Agent pinpointAgent) {
		final Runnable stop = new Runnable() {
			@Override
			public void run() {
				pinpointAgent.stop();
			}
		};
		ELAgentThreadFactory pinpointThreadFactory = new ELAgentThreadFactory(
				"Pinpoint-shutdown-hook");
		Thread thread = pinpointThreadFactory.newThread(stop);
		Runtime.getRuntime().addShutdownHook(thread);
	}

	private void saveLogFilePath(ClassPathResolver classPathResolver) {
		String agentLogFilePath = classPathResolver.getAgentLogFilePath();
		logger.info("logPath:" + agentLogFilePath);

		systemProperty.setProperty(ProductInfo.NAME + ".log", agentLogFilePath);
	}

	private void saveELAgentVersion() {
		logger.info("pinpoint version:" + Version.VERSION);
		systemProperty.setProperty(ProductInfo.NAME + ".version",
				Version.VERSION);
	}

	private String getConfigPath(ClassPathResolver classPathResolver) {
		final String configName = ELConstants.ProductName + ".config";
		String agentConfigFormSystemProperty = System.getProperty(configName);
		if (agentConfigFormSystemProperty != null) {
			logger.info(configName + " systemProperty found. "
					+ agentConfigFormSystemProperty);
			return agentConfigFormSystemProperty;
		}

		String classPathAgentConfigPath = classPathResolver
				.getAgentConfigPath();
		if (classPathAgentConfigPath != null) {
			logger.info("classpath " + configName + " found. "
					+ classPathAgentConfigPath);
			return classPathAgentConfigPath;
		}

		logger.info(configName + " file not found.");
		return null;
	}

	private List<URL> resolveLib(ClassPathResolver classPathResolver) {
		// this method may handle only absolute path, need to handle relative
		// path (./..agentlib/lib)
		String agentJarFullPath = classPathResolver.getAgentJarFullPath();
		String agentLibPath = classPathResolver.getAgentLibPath();
		List<URL> urlList = resolveLib(classPathResolver.resolveLib());
		String agentConfigPath = classPathResolver.getAgentConfigPath();

		if (logger.isInfoEnabled()) {
			logger.info("agent JarPath:" + agentJarFullPath);
			logger.info("agent LibDir:" + agentLibPath);
			for (URL url : urlList) {
				logger.info("agent Lib:" + url);
			}
			logger.info("agent config:" + agentConfigPath);
		}

		return urlList;
	}

	private List<URL> resolveLib(List<URL> urlList) {
		if (DEFAULT_AGENT.equals(getAgentType().toUpperCase())) {
			final List<URL> releaseLib = new ArrayList<URL>(urlList.size());
			for (URL url : urlList) {
				//
				if (!url.toExternalForm().contains("eyelink-profiler-test")) {
					releaseLib.add(url);
				}
			}
			return releaseLib;
		} else {
			logger.info("load " + PLUGIN_TEST_AGENT + " lib");
			// plugin test
			return urlList;
		}
	}

	private String getAgentType() {
		String agentType = agentArgs.get(AGENT_TYPE);
		if (agentType == null) {
			return DEFAULT_AGENT;
		}
		return agentType;

	}
}
