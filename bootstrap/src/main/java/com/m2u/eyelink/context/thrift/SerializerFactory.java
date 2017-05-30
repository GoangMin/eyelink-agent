package com.m2u.eyelink.context.thrift;

public interface SerializerFactory<E> {
	E createSerializer();
}
