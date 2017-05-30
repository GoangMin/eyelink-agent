package com.m2u.eyelink.plugin.tomcat.interceptor;

import static com.m2u.eyelink.trace.ServiceTypeProperty.*;

import com.m2u.eyelink.trace.ServiceType;
import com.m2u.eyelink.trace.ServiceTypeFactory;

public final class TomcatConstants {
	private TomcatConstants() {
    }

    public static final ServiceType TOMCAT = ServiceTypeFactory.of(1010, "TOMCAT", RECORD_STATISTICS);
    public static final ServiceType TOMCAT_METHOD = ServiceTypeFactory.of(1011, "TOMCAT_METHOD");

    public static final String TOMCAT_SERVLET_ASYNC_SCOPE = "TomcatServletAsyncScope";

    public static final String ASYNC_ACCESSOR = "com.navercorp.pinpoint.plugin.tomcat.AsyncAccessor";
    public static final String TRACE_ACCESSOR = "com.navercorp.pinpoint.plugin.tomcat.TraceAccessor";

}