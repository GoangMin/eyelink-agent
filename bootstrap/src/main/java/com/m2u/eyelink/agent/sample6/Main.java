package com.m2u.eyelink.agent.sample6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.sun.tools.attach.VirtualMachine;

// ref : https://github.com/EntryPointKR/Bytecode-Instrumentation
// Exception이 발생하면 BCI가 Exception.class 파일을 Target에 생성함.
public class Main {
	public static void main(String[] args) {
		try {
			String jvm = ManagementFactory.getRuntimeMXBean().getName();
			String pid = jvm.substring(0, jvm.indexOf('@'));
			VirtualMachine vm = VirtualMachine.attach(pid);
			vm.loadAgent(generateJar(Agent.class, Utils.class,
					ClassTransformer.class, TransformClassVisitor.class,
					TransformMethodVisitor.class).getAbsolutePath());
			throw new Exception();
		} catch (Exception ex) {
			// Ignore
		}
	}

	public static File generateJar(Class agent, Class... resources)
			throws IOException {
		File jar = new File("agent.jar");
		jar.deleteOnExit();

		Manifest manifest = new Manifest();
		Attributes attr = manifest.getMainAttributes();

		attr.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		attr.put(new Attributes.Name("Agent-Class"),
				"com.m2u.eyelink.agent.sample6.Agent");
		attr.put(new Attributes.Name("Can-Retransform-Classes"), "true");
		attr.put(new Attributes.Name("Can-Redefine-Classes"), "true");

		JarOutputStream out = new JarOutputStream(new FileOutputStream(jar),
				manifest);
		out.putNextEntry(new JarEntry(Utils.getClassName(Agent.class)));
		out.write(Utils.getBytesAsClass(agent));
		out.closeEntry();

		for (Class cls : resources) {
			String name = Utils.getClassName(cls);
			out.putNextEntry(new JarEntry(name));
			out.write(Utils.getBytesAsClass(cls));
			out.closeEntry();
		}

		out.close();
		return jar;
	}
}
