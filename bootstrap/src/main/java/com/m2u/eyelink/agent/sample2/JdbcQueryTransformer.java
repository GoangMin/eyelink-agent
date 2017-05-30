package com.m2u.eyelink.agent.sample2;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author k
 *
 */
public class JdbcQueryTransformer implements ClassFileTransformer {

	public JdbcQueryTransformer() {
		super();
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		byte[] byteCode = classfileBuffer;

		ClassPool pool = ClassPool.getDefault();
		CtClass currentClass = null;
		CtClass statement = null;

		try {
			currentClass = pool.makeClass(new java.io.ByteArrayInputStream(
					classfileBuffer));
			statement = pool.get("java.sql.Statement");
			if (currentClass.subtypeOf(statement)
					&& !currentClass.isInterface()) {
				probeStatement(currentClass);
			}
			byteCode = currentClass.toBytecode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (currentClass != null) {
				currentClass.detach();
			}
		}
		return byteCode;
	}

	private void probeStatement(CtClass currentClass) throws NotFoundException,
			CannotCompileException {

		CtMethod executeQuery = null;

		try {
			executeQuery = currentClass.getDeclaredMethod("executeQuery");
		} catch (NotFoundException e) {
			return;
		}
		executeQuery
				.insertBefore("try {System.out.println($1);} catch (Exception e){System.out.println(\"no sql\");}");
	}

}
