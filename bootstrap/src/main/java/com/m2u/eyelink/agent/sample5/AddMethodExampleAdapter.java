package com.m2u.eyelink.agent.sample5;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddMethodExampleAdapter extends ClassVisitor {
	private int acc_;
	private String f_name_;
	private String f_desc_;
	private String m_name_;
	private String m_desc_;
	private String c_name_;
	private boolean is_field_present_ = false;
	private boolean is_method_present_ = false;

	public AddMethodExampleAdapter(ClassVisitor cv, int acc, String cname,
			String f_name, String f_desc, String m_name, String m_desc) {
		super(Opcodes.ASM5, cv);
		acc_ = acc;
		f_name_ = f_name;
		f_desc_ = f_desc;
		m_name_ = m_name;
		m_desc_ = m_desc;
		c_name_ = cname;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if (name.equals(f_name_)) {
			is_field_present_ = true;
		}
		return cv.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int acc, String name, String desc,
			String sign, String[] except) {
		if (name.equals(m_name_)) {
			is_method_present_ = true;
		}
		return cv.visitMethod(acc, name, desc, sign, except);
	}

	@Override
	public void visitEnd() {
		if (!is_field_present_) {
			FieldVisitor fv = cv.visitField(acc_, f_name_, f_desc_, null, null);
			if (fv != null) {
				fv.visitEnd();
			}
		}
		if (!is_method_present_) {
			MethodVisitor mv = cv.visitMethod(acc_, m_name_, m_desc_, null,
					null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ILOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(Opcodes.IFLE, l0);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ILOAD, 1);
			mv.visitFieldInsn(Opcodes.PUTFIELD, c_name_, f_name_, f_desc_);
			Label l1 = new Label();
			mv.visitJumpInsn(Opcodes.GOTO, l1);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException");
			mv.visitInsn(Opcodes.DUP);
			mv.visitLdcInsn("field must be greater than 0");
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
					"java/lang/IllegalArgumentException", "<init>",
					"(Ljava/lang/String;)V", false);
			mv.visitInsn(Opcodes.ATHROW);
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(3, 2);
			mv.visitEnd();
		}
		cv.visitEnd();
	}

}
