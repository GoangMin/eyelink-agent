package com.m2u.eyelink.plugin.tomcat;

import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.trace.MethodType;

public class ServletAsyncMethodDescriptor implements MethodDescriptor {
	   private int apiId = 0;
	    private int type = MethodType.WEB_REQUEST;

	    @Override
	    public String getMethodName() {
	        return "";
	    }

	    @Override
	    public String getClassName() {
	        return "";
	    }

	    @Override
	    public String[] getParameterTypes() {
	        return null;
	    }

	    @Override
	    public String[] getParameterVariableName() {
	        return null;
	    }

	    @Override
	    public String getParameterDescriptor() {
	        return "()";
	    }

	    @Override
	    public int getLineNumber() {
	        return -1;
	    }

	    @Override
	    public String getFullName() {
	        return ServletAsyncMethodDescriptor.class.getName();
	    }

	    @Override
	    public void setApiId(int apiId) {
	        this.apiId = apiId;
	    }

	    @Override
	    public int getApiId() {
	        return apiId;
	    }

	    @Override
	    public String getApiDescriptor() {
	        return "Tomcat Servlet Asynchronous Process";
	    }

	    public int getType() {
	        return type;
	    }

	    public void setType(int type) {
	        this.type = type;
	    }
}
