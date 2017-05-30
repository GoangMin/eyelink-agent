package com.m2u.eyelink.agent;

import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarFile;

import com.m2u.eyelink.common.ELConstants;
import com.m2u.eyelink.logging.ELLogger;


public class ELAgent {
	private static final ELLogger logger = ELLogger.getLogger(ELAgent.class
			.getName());

	public static void premain(String agentArgs, Instrumentation inst) {
		logger.info("#### Intercept EyeLink Java Agent!!!! ###");
		if (agentArgs == null) {
			agentArgs = "";
		}
		logger.info(ELConstants.ProductName + " agentArgs:" + agentArgs);

		// TODO 이미 Agent가 실행중일 경우 Skip 처리 로직 필요.
		// final boolean success = STATE.start();
		// if (!success) {
		// logger.warn("pinpoint-bootstrap already started. skipping agent loading.");
		// return;
		// }

		// Agent Arguement Parsing
		Map<String, String> agentArgsMap = argsParser(agentArgs);
		if (!agentArgsMap.isEmpty()) {
			logger.info("agentParameter :" + agentArgs);
		}

		final ClassPathResolver classPathResolver = new AgentDirBaseClassPathResolver();
		if (!classPathResolver.verify()) {
			logger.warn("Agent Directory Verify fail. skipping agent loading.");
			logELAgentLoadFail();
			return;
		}

		// Load Agent Class in ELAgent.jar 
		ELAgentJarFile agentJarFile = classPathResolver.getELAgentJarFile();
		appendToELAgentClassLoader(inst, agentJarFile);

		logger.info("===========>ELAgentStarter before");
		ELAgentStarter elagent = new ELAgentStarter(agentArgsMap, agentJarFile, classPathResolver, inst);
		if (!elagent.start()) {
			logELAgentLoadFail();
		}
	}

	private static void appendToELAgentClassLoader(
			Instrumentation instrumentation, ELAgentJarFile agentJarFile) {
		List<JarFile> jarFileList = agentJarFile.getJarFileList();
		for (JarFile jarFile : jarFileList) {
			logger.info("appendToELAgentClassLoader:" + jarFile.getName());
			instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
		}
	}

	public static Map<String, String> argsParser(String args) {
		if (args == null || args.isEmpty()) {
			return Collections.emptyMap();
		}

		final Map<String, String> map = new HashMap<String, String>();

		Scanner scanner = new Scanner(args);
		scanner.useDelimiter("\\s*,\\s*");

		while (scanner.hasNext()) {
			String token = scanner.next();
			int assign = token.indexOf('=');

			if (assign == -1) {
				map.put(token, "");
			} else {
				String key = token.substring(0, assign);
				String value = token.substring(assign + 1);
				map.put(key, value);
			}
		}
		scanner.close();
		return Collections.unmodifiableMap(map);
	}

	private static void logELAgentLoadFail() {
		final String errorLog = "*****************************************************************************\n"
				+ "* EyeLink Agent load failure\n"
				+ "*****************************************************************************";
		System.err.println(errorLog);
	}
}
