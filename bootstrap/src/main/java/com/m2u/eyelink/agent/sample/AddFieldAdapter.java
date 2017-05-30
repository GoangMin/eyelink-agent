package com.m2u.eyelink.agent.sample;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class AddFieldAdapter extends ClassVisitor {
	private int fAcc;
	private String fName;
	private String fDesc;
	private boolean isFieldPresent;

	public AddFieldAdapter(int fAcc, ClassVisitor cv, String fName, String fDesc) {
		super(Opcodes.ASM4, cv);
		this.fAcc = fAcc;
		this.fName = fName;
		this.fDesc = fDesc;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if (name.equals(fName)) {
			isFieldPresent = true;
		}
		return cv.visitField(access, name, desc, signature, value);
	}

	@Override
	public void visitEnd() {
		if (!isFieldPresent) {
			FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
			if (fv != null) {
				fv.visitEnd();
			}
		}
		cv.visitEnd();
	}
}

// 출처: http://devilnangel.tistory.com/80 [I, My, Me, Mine and MySelf]