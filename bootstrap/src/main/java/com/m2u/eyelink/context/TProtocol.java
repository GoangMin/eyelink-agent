package com.m2u.eyelink.context;

import java.util.BitSet;

public interface TProtocol {
	public BitSet readBitSet(int i) ;
	public int readI32() ;
	public long readI64();

	public short readI16() ;
	public String readString();

	public void writeBitSet(BitSet optionals, int i) ;
	public void readStructBegin();
	public TField readFieldBegin();
	public void readFieldEnd();
	public void readStructEnd();

}
