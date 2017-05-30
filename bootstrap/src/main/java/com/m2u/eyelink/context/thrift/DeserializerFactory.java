package com.m2u.eyelink.context.thrift;

public interface DeserializerFactory<T> {
	T createDeserializer();
}
