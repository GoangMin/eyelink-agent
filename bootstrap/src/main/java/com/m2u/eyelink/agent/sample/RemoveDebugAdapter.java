package com.m2u.eyelink.agent.sample;

//출처: http://devilnangel.tistory.com/79 [I, My, Me, Mine and MySelf]
import org.objectweb.asm.ClassVisitor;

public class RemoveDebugAdapter extends ClassVisitor {
	/**
	 * * Constructs a new {@link org.objectweb.asm.ClassVisitor}. * * @param api
	 * the ASM API version implemented by this visitor. Must be one * of
	 * {@link Opcodes#ASM4} or {@link Opcodes#ASM5}. * @param cv the class
	 * visitor to which this visitor must delegate method
	 */
	public RemoveDebugAdapter(int api, ClassVisitor cv) {
		super(api, cv);
	}

	/**
	 * * Visits the source of the class. * * @param source * the name of the
	 * source file from which the class was compiled. * May be <tt>null</tt>. * @param
	 * debug * additional debug information to compute the correspondance *
	 * between source and compiled elements of the class. May be * <tt>null</tt>
	 * .
	 */
	@Override
	public void visitSource(String source, String debug) {
	}

	/**
	 * * Visits the enclosing class of the class. This method must be called
	 * only * if the class has an enclosing class. * * @param owner * internal
	 * name of the enclosing class of the class. * @param name * the name of the
	 * method that contains the class, or * <tt>null</tt> if the class is not
	 * enclosed in a method of its * enclosing class. * @param desc * the
	 * descriptor of the method that contains the class, or * <tt>null</tt> if
	 * the class is not enclosed in a method of its * enclosing class.
	 */
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
	}

	/**
	 * * Visits information about an inner class. This inner class is not *
	 * necessarily a member of the class being visited. * * @param name * the
	 * internal name of an inner class (see *
	 * {@link org.objectweb.asm.Type#getInternalName() getInternalName}). * @param
	 * outerName * the internal name of the class to which the inner class *
	 * belongs (see {@link org.objectweb.asm.Type#getInternalName()
	 * getInternalName}). * May be <tt>null</tt> for not member classes. * @param
	 * innerName * the (simple) name of the inner class inside its enclosing *
	 * class. May be <tt>null</tt> for anonymous inner classes. * @param access *
	 * the access flags of the inner class as originally declared in * the
	 * enclosing class.
	 */
	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
	}
}
