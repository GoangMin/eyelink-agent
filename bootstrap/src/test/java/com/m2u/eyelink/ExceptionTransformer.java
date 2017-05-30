package com.m2u.eyelink;

import java.io.FileOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ExceptionTransformer implements Opcodes {
	// Convert class
	public void transform(String newClassName) throws Exception {

		System.out.println("Starting transformation of java.lang.Exception...");

		// Reader
		ClassReader reader = new ClassReader("java.lang.Exception");

		// Writer
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		// Class Visitor
		ClassVisitor adapter = new ExceptionClassAdapter(0, writer);

		reader.accept(adapter, ClassReader.SKIP_FRAMES);

		byte[] b = writer.toByteArray();
		FileOutputStream fos = new FileOutputStream(newClassName + ".class");
		fos.write(b);
		fos.flush();

	}

	public static void main(String[] args) {
		try {
			String newClassName = "Exception";
			if (args.length >= 1)
				newClassName = args[0];
			ExceptionTransformer fit = new ExceptionTransformer();
			fit.transform(newClassName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	class ExceptionClassAdapter extends ClassVisitor implements Opcodes {

		public ExceptionClassAdapter(int api, ClassVisitor cv) {
			super(api, cv);
		}

	}
}
