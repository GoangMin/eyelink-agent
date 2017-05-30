package com.m2u.eyelink.agent.sample;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ELClassFileTransformer3 implements ClassFileTransformer {

	public ELClassFileTransformer3() {
		super();
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
			
		// Method Filter 적
		ClassReader cr = new ClassReader(classfileBuffer); 
		ClassWriter cw = new ClassWriter(cr, 0); 
		ChangeVersionAdapter ca = new ChangeVersionAdapter(Opcodes.ASM5, cw); 
		cr.accept(ca, 0); 

			
		return cw.toByteArray(); // ret represents the same class as b
	}

}
