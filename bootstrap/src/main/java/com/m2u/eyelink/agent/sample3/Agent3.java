package com.m2u.eyelink.agent.sample3;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Agent3 {
	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new ClassFileTransformer() {
			public byte[] transform(ClassLoader l, String name, Class c,
					ProtectionDomain d, byte[] b)
					throws IllegalClassFormatException {
				ClassWriter cw = new ClassWriter(0);
				ClassReader cr = new ClassReader(b);
				cr.accept(cw, 0);
				return cw.toByteArray(); // ret represents the same class as b
			}
		});
	}
}
//출처: http://devilnangel.tistory.com/78 [I, My, Me, Mine and MySelf]


