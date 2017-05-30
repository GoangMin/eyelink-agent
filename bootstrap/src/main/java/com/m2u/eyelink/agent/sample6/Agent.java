package com.m2u.eyelink.agent.sample6;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;


//ref : https://github.com/EntryPointKR/Bytecode-Instrumentation
public class Agent {
    public static void agentmain(String agentArg, Instrumentation inst) throws Exception {
        inst.addTransformer(new ClassTransformer());

        InputStream inStream = ClassLoader.getSystemResourceAsStream("java/lang/Exception.class");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int read;
        byte[] data = new byte[65536];
        while ((read = inStream.read(data, 0, data.length)) != -1) {
            outStream.write(data, 0, read);
        }

        inst.redefineClasses(new ClassDefinition(Exception.class, outStream.toByteArray()));
    }
}
