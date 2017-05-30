package com.m2u.eyelink.agent.sample;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

//출처: http://devilnangel.tistory.com/79 [I, My, Me, Mine and MySelf]

public class RemoveMethodAdapter extends ClassVisitor {
	private String name_;
	private String desc_;

	/**
	 * * Constructs a new {@link org.objectweb.asm.ClassVisitor}. 
	 * * * @param api 
	 * the ASM API version implemented by this visitor. Must be one * of
	 * {@link Opcodes#ASM4} or {@link Opcodes#ASM5}. * @param cv the class
	 * visitor to which this visitor must delegate method
	 */

	public RemoveMethodAdapter(int api, ClassVisitor cv, String name,
			String desc) {
		super(api, cv);
		name_ = name;
		desc_ = desc;
	}

	/**
	 * * Visits a method of the class. This method <i>must</i> return a new *
	 * {@link org.objectweb.asm.MethodVisitor} instance (or <tt>null</tt>) each
	 * time it is called, * i.e., it should not return a previously returned
	 * visitor. * * @param access * the method's access flags (see
	 * {@link org.objectweb.asm.Opcodes}). This * parameter also indicates if
	 * the method is synthetic and/or * deprecated. * @param name * the method's
	 * name. * @param desc * the method's descriptor (see
	 * {@link org.objectweb.asm.Type Type}). * @param signature * the method's
	 * signature. May be <tt>null</tt> if the method * parameters, return type
	 * and exceptions do not use generic * types. * @param exceptions * the
	 * internal names of the method's exception classes (see *
	 * {@link org.objectweb.asm.Type#getInternalName() getInternalName}). May be
	 * * <tt>null</tt>. * @return an object to visit the byte code of the
	 * method, or <tt>null</tt> * if this class visitor is not interested in
	 * visiting the code of * this method.
	 */

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		if (cv != null) {
			if (name.equals(name_) && desc.equals(desc_)) {
				// do not delegate to next visitor -> this remove the method
				return null;
			}

			return cv.visitMethod(access, name, desc, signature, exceptions);
		}
		return null;
	}
}
