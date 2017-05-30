package com.m2u.eyelink.context.thrift;


public enum TThreadDumpType implements org.apache.thrift.TEnum {
	  TARGET(0),
	  PENDING(1);

	  private final int value;

	  private TThreadDumpType(int value) {
	    this.value = value;
	  }

	  /**
	   * Get the integer value of this enum value, as defined in the Thrift IDL.
	   */
	  public int getValue() {
	    return value;
	  }

	  /**
	   * Find a the enum type by its integer value, as defined in the Thrift IDL.
	   * @return null if the value is not found.
	   */
	  public static TThreadDumpType findByValue(int value) { 
	    switch (value) {
	      case 0:
	        return TARGET;
	      case 1:
	        return PENDING;
	      default:
	        return null;
	    }
	  }
	}
