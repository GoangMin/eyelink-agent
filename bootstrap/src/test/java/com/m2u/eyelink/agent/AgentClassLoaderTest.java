package com.m2u.eyelink.agent;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.DefaultAgent;
import com.m2u.eyelink.common.service.DefaultAnnotationKeyRegistryService;
import com.m2u.eyelink.common.service.DefaultServiceTypeRegistryService;
import com.m2u.eyelink.plugin.tomcat.DefaultProfilerConfig;

public class AgentClassLoaderTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Test
	public void boot_DummyAgent() throws IOException, ClassNotFoundException {
		AgentClassLoader agentClassLoader = new AgentClassLoader(new URL[0]);
		agentClassLoader.setBootClass("com.m2u.eyelink.agent.DummyAgent");
		AgentOption option = new DefaultAgentOption(new DummyInstrumentation(),
				"testCaseAgent", "testCaseAppName",
				new DefaultProfilerConfig(), new URL[0], null,
				new DefaultServiceTypeRegistryService(),
				new DefaultAnnotationKeyRegistryService());
		agentClassLoader.boot(option);

		// TODO need verification - implementation for obtaining logger changed
		// PLoggerBinder loggerBinder = (PLoggerBinder)
		// agentClassLoader.initializeLoggerBinder();
		// PLogger test = loggerBinder.getLogger("test");
		// test.info("slf4j logger test");

	}

	@Test
	public void boot_DefaultAgent() throws IOException, ClassNotFoundException {
		AgentClassLoader agentClassLoader = new AgentClassLoader(new URL[0]);
		agentClassLoader.setBootClass("com.m2u.eyelink.agent.profiler.DefaultAgent");
		AgentOption option = new DefaultAgentOption(new DummyInstrumentation(),
				"testCaseAgent", "testCaseAppName",
				new DefaultProfilerConfig(), new URL[0], null,
				new DefaultServiceTypeRegistryService(),
				new DefaultAnnotationKeyRegistryService());
		agentClassLoader.boot(option);

		// TODO need verification - implementation for obtaining logger changed
		// PLoggerBinder loggerBinder = (PLoggerBinder)
		// agentClassLoader.initializeLoggerBinder();
		// PLogger test = loggerBinder.getLogger("test");
		// test.info("slf4j logger test");

	}

	private String getProjectLibDir() {
		// not really necessary, but useful for testing protectionDomain
		ProtectionDomain protectionDomain = AgentClassLoader.class
				.getProtectionDomain();
		CodeSource codeSource = protectionDomain.getCodeSource();
		URL location = codeSource.getLocation();

		logger.info("lib location:" + location);
		String path = location.getPath();
		// file:/D:/nhn_source/pinpoint_project/pinpoint-tomcat-profiler/target/classes/
		int dirPath = path.lastIndexOf("target/classes/");
		if (dirPath == -1) {
			throw new RuntimeException("target/classes/ not found");
		}
		String projectDir = path.substring(1, dirPath);
		return projectDir + "src/test/lib";
	}
}
