package com.m2u.eyelink.agent.sample;

import java.lang.instrument.Instrumentation;

import com.m2u.eyelink.agent.sample2.JdbcQueryTransformer;

public class ELAgentTest {
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("#### Call EyeLink Java Agent!!!! ###");
		// instrumentation.addTransformer(new ClassFileTransformer() {
		// public byte[] transform(ClassLoader l, String name, Class c,
		// ProtectionDomain d, byte[] b)
		// throws IllegalClassFormatException {
		// byte[] ret = null;
		//
		// /* TODO do somthing and fill ret */
		//
		// return ret;
		// }
		// });

		// ASM 01 : No Action
//		inst.addTransformer(new ELClassFileTransformer());

		// ASM 02 : Change Version
//		inst.addTransformer(new ELClassFileTransformer2());

		// ASM 03 : Method Filter
//		inst.addTransformer(new ELClassFileTransformer3());
		
		// ASM 04 : Delete Member
//		inst.addTransformer(new ELClassFileTransformer4());
	
		// ASM 05 : Delete Method
//		inst.addTransformer(new ELClassFileTransformer5());

		// ASM 06 : Add Class Member
		inst.addTransformer(new ELClassFileTransformer6());
		
		// Javassist 사용 
//		inst.addTransformer(new JdbcQueryTransformer());
	}

//	public static void agentmain(String args, Instrumentation inst) {
//		inst.addTransformer(new JdbcQueryTransformer());
//	}
}
