package com.m2u.eyelink.test;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.AgentDirBaseClassPathResolver;

public class TestAgentDirBaseClassPathResolver {
	private static final Logger logger = LoggerFactory.getLogger(TestAgentDirBaseClassPathResolver.class
			.getName());
	
    private static final String ELAgent_JAR = "eyelink-agent-1.0.0-SNAPSHOT.jar";
    private static final String TEST_AGENT_DIR = "";
    private static final String SEPARATOR = File.separator;
    
    private static final AtomicInteger AGENT_ID_ALLOCATOR = new AtomicInteger();
    
    private static String agentBuildDir;
    private static String agentBootstrapPath;
    
	@BeforeClass
	public static void beforeClass() throws Exception {
		logger.debug("beforeClass start");
		String classLocation = getClassLocation(TestAgentDirBaseClassPathResolver.class);
		logger.debug("buildDir:{}", classLocation);

//		agentBuildDir = classLocation + SEPARATOR + TEST_AGENT_DIR + '_'
//				+ AGENT_ID_ALLOCATOR.incrementAndGet();
		agentBuildDir = classLocation + "/..";

		logger.debug("agentBuildDir:{}", agentBuildDir);

		agentBootstrapPath = agentBuildDir + SEPARATOR + ELAgent_JAR;

		logger.debug("agentPath:{}", agentBootstrapPath);

//		createAgentDir(agentBuildDir);

		logger.debug("beforeClass end");
	}

//    private static void createAgentDir(String tempAgentDir) throws IOException {
//
//        agentDirGenerator = new AgentDirGenerator(tempAgentDir);
//        agentDirGenerator.create();
//
//    }
    
    private static String getClassLocation(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        URL location = codeSource.getLocation();
        logger.debug("codeSource.getLocation:{}", location);
        File file = FileUtils.toFile(location);
        return file.getPath();
    }
    
	@AfterClass
	public static void afterClass() throws Exception {
	}

	@Test
	public void testFindAgentJar() throws Exception {

		logger.debug("TEST_AGENT_DIR:{}", agentBuildDir);
		logger.debug("agentPath:{}", agentBootstrapPath);

		AgentDirBaseClassPathResolver classPathResolver = new AgentDirBaseClassPathResolver(
				agentBootstrapPath);
		Assert.assertTrue("verify agent directory ", classPathResolver.verify());

		boolean findAgentJar = classPathResolver.findAgentJar();
		Assert.assertTrue(findAgentJar);

		String agentJar = classPathResolver.getAgentJarName();
		Assert.assertEquals(ELAgent_JAR, agentJar);

		String agentPath = classPathResolver.getAgentJarFullPath();
		Assert.assertEquals(agentBootstrapPath, agentPath);

//		String agentDirPath = classPathResolver.getAgentDirPath();
//		Assert.assertEquals(agentBuildDir, agentDirPath);

//		String agentLibPath = classPathResolver.getAgentLibPath();
//		Assert.assertEquals(agentBuildDir + File.separator + "lib",
//				agentLibPath);

//		BootstrapJarFile bootstrapJarFile = classPathResolver
//				.getBootstrapJarFile();
//		closeJarFile(bootstrapJarFile);

	}
	
	@Test
	public void testPattern() throws Exception {
		String pattern = "^[a-zA-Z0-9]*$";
        String input = "ABzzzDAWRAWR0";

        String VERSION_PATTERN = "(-[0-9]+\\.[0-9]+\\.[0-9]+((\\-SNAPSHOT)|(-RC[0-9]+))?)?";
//        VERSION_PATTERN = "(-[0-9]+\\.[0-9]).0-SNAPSHOT";
        input = "eyelink-agent-1.0.0-SNAPSHOT.jar";
        pattern = "eyelink-agent" + VERSION_PATTERN + "\\.jar";

        boolean isTrue;
        Pattern agentPattern;
        
        agentPattern = Pattern.compile(pattern);
        Matcher matcher = agentPattern.matcher(input);
        isTrue = matcher.find();
        Assert.assertTrue(isTrue);

        
        isTrue = Pattern.matches(pattern, input);
        if(isTrue) {
            System.out.println(input+"는 패턴에 일치함.");
        }
        else {
            System.out.println("패턴 일치하지 않음.");
        }
        Assert.assertTrue(isTrue);

	}
}
