package com.m2u.eyelink.agent.sample3;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {
	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new ClassFileTransformer() {
			public byte[] transform(ClassLoader l, String name, Class c,
					ProtectionDomain d, byte[] b)
					throws IllegalClassFormatException {
				byte[] ret = null; 
				/* do somthing and fill ret */
				return ret;
			}
		});
	}

	// 출처: http://devilnangel.tistory.com/78 [I, My, Me, Mine and MySelf]
}
