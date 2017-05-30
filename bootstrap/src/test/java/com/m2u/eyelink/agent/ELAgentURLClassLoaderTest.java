package com.m2u.eyelink.agent;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assert;
import org.junit.Test;

public class ELAgentURLClassLoaderTest {

	@Test
	public void testOnLoadClass() throws Exception {

		ELAgentURLClassLoader cl = new ELAgentURLClassLoader(new URL[] {},
				Thread.currentThread().getContextClassLoader());
		try {
			cl.loadClass("com.m2u.eyelink.agent.profiler.DefaultAgent");
			Assert.fail();
		} catch (ClassNotFoundException ignored) {
		}

		// try {
		// cl.loadClass("com.navercorp.pinpoint.profiler.DefaultAgent");
		// } catch (ClassNotFoundException e) {
		//
		// }
		// should be able to test using the above code, but it is not possible
		// from bootstrap testcase.
		// it could be possible by specifying the full path to the URL
		// classloader, but it would be harder to maintain.
		// for now, just test if DefaultAgent is specified to be loaded
		Assert.assertTrue(cl
				.onLoadClass("com.m2u.eyelink.agent.profiler.DefaultAgent"));
	}


	// classloader를 이용한 jar 파일 Loading Test
	@Test
	public void testJarFile() throws MalformedURLException, ClassNotFoundException {
		File file  = new File("/Users/baesunghan/git/m2u/pinpoint/quickstart/agent/target/pinpoint-agent/pinpoint-bootstrap-1.6.1-SNAPSHOT.jar");

		URL url = file.toURL();  
		URL[] urls = new URL[]{url};

		ClassLoader cl = new URLClassLoader(urls);
		Class cls = cl.loadClass("org.jboss.netty.channel.group.ChannelGroup");
		Class cls2 = cl.loadClass("com.navercorp.pinpoint.bootstrap.LoadState");
	}
}
