package com.m2u.eyelink.agent.sample;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ELClassFileTransformer2 implements ClassFileTransformer {

	public ELClassFileTransformer2() {
		super();
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		System.out.println("className : " + className);
		
		ClassWriter cw = new ClassWriter(0);

		ChangeVersionAdapter cv = new ChangeVersionAdapter(Opcodes.ASM5, cw);

		ClassReader cr = new ClassReader(classfileBuffer);
		cr.accept(cv, 0);
			
		return cw.toByteArray(); // ret represents the same class as b
	}

}
