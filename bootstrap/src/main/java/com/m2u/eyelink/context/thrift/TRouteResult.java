package com.m2u.eyelink.context.thrift;

public enum TRouteResult implements org.apache.thrift.TEnum {
	  OK(0),
	  BAD_REQUEST(200),
	  EMPTY_REQUEST(201),
	  NOT_SUPPORTED_REQUEST(202),
	  BAD_RESPONSE(210),
	  EMPTY_RESPONSE(211),
	  NOT_SUPPORTED_RESPONSE(212),
	  TIMEOUT(220),
	  NOT_FOUND(230),
	  NOT_ACCEPTABLE(240),
	  NOT_SUPPORTED_SERVICE(241),
	  UNKNOWN(-1);

	  private final int value;

	  private TRouteResult(int value) {
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
	  public static TRouteResult findByValue(int value) { 
	    switch (value) {
	      case 0:
	        return OK;
	      case 200:
	        return BAD_REQUEST;
	      case 201:
	        return EMPTY_REQUEST;
	      case 202:
	        return NOT_SUPPORTED_REQUEST;
	      case 210:
	        return BAD_RESPONSE;
	      case 211:
	        return EMPTY_RESPONSE;
	      case 212:
	        return NOT_SUPPORTED_RESPONSE;
	      case 220:
	        return TIMEOUT;
	      case 230:
	        return NOT_FOUND;
	      case 240:
	        return NOT_ACCEPTABLE;
	      case 241:
	        return NOT_SUPPORTED_SERVICE;
	      case -1:
	        return UNKNOWN;
	      default:
	        return UNKNOWN;
	    }
	  }
	}
