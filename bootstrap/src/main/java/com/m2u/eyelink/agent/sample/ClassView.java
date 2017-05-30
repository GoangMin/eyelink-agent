package com.m2u.eyelink.agent.sample;

import org.objectweb.asm.*;

public class ClassView {
	// 출처: http://devilnangel.tistory.com/76 [I, My, Me, Mine and MySelf]
	public static void main(String[] args) {
		try {
			ClassPrinter cp = new ClassPrinter();
			ClassReader cr = new ClassReader("java.lang.Runnable");
//			ClassReader cr = new ClassReader("com.m2u.eyelink.agent.sample.HelloWorld");
			
			cr.accept(cp, 0);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

}
