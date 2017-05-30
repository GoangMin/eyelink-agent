package com.m2u.eyelink.agent.profiler.receiver;

import com.m2u.eyelink.context.thrift.CommandHeaderTBaseDeserializerFactory;
import com.m2u.eyelink.context.thrift.CommandHeaderTBaseSerializerFactory;


public class CommandSerializer {
    public static final CommandHeaderTBaseSerializerFactory SERIALIZER_FACTORY = new CommandHeaderTBaseSerializerFactory();
    public static final CommandHeaderTBaseDeserializerFactory DESERIALIZER_FACTORY = new CommandHeaderTBaseDeserializerFactory();

}
