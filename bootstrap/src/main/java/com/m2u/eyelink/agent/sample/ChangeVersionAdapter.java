package com.m2u.eyelink.agent.sample;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeVersionAdapter extends ClassVisitor {
	/**
	 * * Constructs a new {@link org.objectweb.asm.ClassVisitor}. * * @param api
	 * the ASM API version implemented by this visitor. Must be one * of
	 * {@link Opcodes#ASM4} or {@link Opcodes#ASM5}. * @param cv the class
	 * visitor to which this visitor must delegate method
	 */
	public ChangeVersionAdapter(int api, ClassVisitor cv) {
		
		super(api, cv);
		System.out.println("api : " + api);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		System.out.println("version : " + version);
		System.out.println("access : " + access);
		System.out.println("name : " + name);
		System.out.println("signature : " + signature);
		System.out.println("superName : " + superName);
		System.out.println("interfaces : " + interfaces);
		if (cv != null) {
			cv.visit(Opcodes.V1_5, access, name, signature, superName,
					interfaces);
		}
	}
}
