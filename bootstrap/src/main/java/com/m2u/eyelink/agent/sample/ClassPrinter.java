package com.m2u.eyelink.agent.sample;

import org.objectweb.asm.*;

public class ClassPrinter extends ClassVisitor {
	// 출처: http://devilnangel.tistory.com/76 [I, My, Me, Mine and MySelf]
	public ClassPrinter() {
		super(Opcodes.ASM4);
	}

	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		System.out.println("visit");
		System.out.println(name + " extends " + superName + " {");
	}

	public void visitSource(String source, String debug) {
		System.out.println("visitSource");
	}

	public void visitOuterClass(String owner, String name, String desc) {
		System.out.println("visitOuterClass");
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		System.out.println("visitAnnotation");
		return null;
	}

	public void visitAttribute(Attribute attr) {
		System.out.println("visitAttribute");
	}

	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
		System.out.println("visitInnerClass");
	}

	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		System.out.println("visitField");
		System.out.println(" " + desc + " " + name);
		return null;
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		System.out.println("visitMethod");
		System.out.println(" " + name + desc);
		return null;
	}

	public void visitEnd() {
		System.out.println("visitEnd");
		System.out.println("}");
	}

}
