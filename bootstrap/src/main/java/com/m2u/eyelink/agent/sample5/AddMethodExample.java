package com.m2u.eyelink.agent.sample5;

import java.io.FileOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;


// 실행 : args로 AddMethodExample를 준다.
// 실행 결과는 AddMethodExample.class가 생성된다.

public class AddMethodExample {
	public static void main(String args[]) throws Exception {
		String c_name = args[0].replace(".class", "");
		ClassWriter cw = new ClassWriter(0);
		ClassReader cr = new ClassReader(c_name);
		AddMethodExampleAdapter cv = new AddMethodExampleAdapter(cw,
				Opcodes.ACC_PUBLIC, c_name, "aaafield", "I", "setField", "(I)V");
		cr.accept(cv, 0);
		FileOutputStream stream = new FileOutputStream("./target/"+c_name + ".class");
		stream.write(cw.toByteArray());
	}

}
