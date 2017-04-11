package com.m2u.eyelink.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.m2u.eyelink.agent.sample.ChangeVersionAdapter;

public class ELClassFileTransformer implements ClassFileTransformer {

	public ELClassFileTransformer() {
		super();
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		
		// TODO Auto-generated method stub
		ClassWriter cw = new ClassWriter(0);

		// cv forwards all events to cw 
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {};

		ClassReader cr = new ClassReader(classfileBuffer);
		cr.accept(cv, 0);
		
			
		return cw.toByteArray(); // ret represents the same class as b
	}

}
