package com.m2u.eyelink.agent.sample;

import java.io.FileOutputStream;

import org.objectweb.asm.*;

public class ChangeVersion {
	// 출처: http://devilnangel.tistory.com/76 [I, My, Me, Mine and MySelf]
	public static void main(String[] args) {
		try {
			ClassWriter cw = new ClassWriter(0);
//			ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {};
			ChangeVersionAdapter cv = new ChangeVersionAdapter(Opcodes.ASM4, cw) {};
			ClassReader cr = new ClassReader("com.m2u.eyelink.agent.sample.HelloWorld");
			
			cr.accept(cv, 0);
			FileOutputStream stream = null;
			try {
				stream = new FileOutputStream("./target/HelloWorld2.class");
				stream.write(cw.toByteArray());
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

}
