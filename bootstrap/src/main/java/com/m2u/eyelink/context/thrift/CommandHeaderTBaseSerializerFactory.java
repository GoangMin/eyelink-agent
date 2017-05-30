package com.m2u.eyelink.context.thrift;

import java.util.Arrays;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public final class CommandHeaderTBaseSerializerFactory implements SerializerFactory<HeaderTBaseSerializer> {

    public static final int DEFAULT_SERIALIZER_MAX_SIZE = 1024 * 64;

    private final SerializerFactory<HeaderTBaseSerializer> factory;

    public CommandHeaderTBaseSerializerFactory() {
        this(DEFAULT_SERIALIZER_MAX_SIZE);
    }

    public CommandHeaderTBaseSerializerFactory(int outputStreamSize) {
        TBaseLocator commandTbaseLocator = new TCommandRegistry(Arrays.asList(TCommandType.values()));

        TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
        HeaderTBaseSerializerFactory serializerFactory = new HeaderTBaseSerializerFactory(true, outputStreamSize, protocolFactory, commandTbaseLocator);

        this.factory = new ThreadLocalHeaderTBaseSerializerFactory<HeaderTBaseSerializer>(serializerFactory);
    }

    @Override
    public HeaderTBaseSerializer createSerializer() {
        return this.factory.createSerializer();
    }

}
