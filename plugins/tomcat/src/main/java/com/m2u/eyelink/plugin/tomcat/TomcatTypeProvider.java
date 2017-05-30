package com.m2u.eyelink.plugin.tomcat;

import com.m2u.eyelink.plugin.tomcat.interceptor.TomcatConstants;
import com.m2u.eyelink.trace.TraceMetadataProvider;
import com.m2u.eyelink.trace.TraceMetadataSetupContext;

public class TomcatTypeProvider implements TraceMetadataProvider {

    @Override
    public void setup(TraceMetadataSetupContext context) {
        context.addServiceType(TomcatConstants.TOMCAT);
        context.addServiceType(TomcatConstants.TOMCAT_METHOD);
    }
}
