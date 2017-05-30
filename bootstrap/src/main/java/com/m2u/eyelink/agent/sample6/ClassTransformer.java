package com.m2u.eyelink.agent.sample6;

import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ASM5;

//ref : https://github.com/EntryPointKR/Bytecode-Instrumentation
public class ClassTransformer implements ClassFileTransformer {
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] ret = classfileBuffer;
		if (className.equals("java/lang/Exception")) {
			try {
				ClassReader reader = new ClassReader("java.lang.Exception");
				ClassWriter writer = new ClassWriter(0);
				ClassVisitor visitor = new TransformClassVisitor(ASM5, writer);

				reader.accept(visitor, 0);
				ret = writer.toByteArray();

				FileOutputStream out = new FileOutputStream("./target/Exception.class");
				out.write(ret);
				out.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return ret;
	}
}
