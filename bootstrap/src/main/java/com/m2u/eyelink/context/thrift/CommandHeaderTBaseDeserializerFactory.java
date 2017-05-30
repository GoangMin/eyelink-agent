package com.m2u.eyelink.context.thrift;

import java.util.Arrays;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public final class CommandHeaderTBaseDeserializerFactory implements DeserializerFactory<HeaderTBaseDeserializer> {

    private final DeserializerFactory<HeaderTBaseDeserializer> factory;

    public CommandHeaderTBaseDeserializerFactory() {
        TBaseLocator commandTbaseLocator = commandTbaseLocator = new TCommandRegistry(Arrays.asList(TCommandType.values()));

        TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
        HeaderTBaseDeserializerFactory deserializerFactory = new HeaderTBaseDeserializerFactory(protocolFactory, commandTbaseLocator);

        this.factory = new ThreadLocalHeaderTBaseDeserializerFactory<HeaderTBaseDeserializer>(deserializerFactory);
    }

    @Override
    public HeaderTBaseDeserializer createDeserializer() {
        return this.factory.createDeserializer();
    }

}
