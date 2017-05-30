package com.m2u.eyelink.common.service;

import java.net.URL;
import java.util.List;

import com.m2u.eyelink.logging.CommonLoggerFactory;
import com.m2u.eyelink.logging.StdoutCommonLoggerFactory;
import com.m2u.eyelink.trace.AnnotationKey;
import com.m2u.eyelink.trace.ServiceTypeInfo;
import com.m2u.eyelink.trace.TraceMetadataLoader;
import com.m2u.eyelink.trace.TraceMetadataProvider;
import com.m2u.eyelink.util.ClassLoaderUtils;

public class DefaultTraceMetadataLoaderService implements TraceMetadataLoaderService {

    private final TraceMetadataLoader loader;

    public DefaultTraceMetadataLoaderService() {
        this(ClassLoaderUtils.getDefaultClassLoader(), StdoutCommonLoggerFactory.INSTANCE);
    }

    public DefaultTraceMetadataLoaderService(CommonLoggerFactory commonLoggerFactory) {
        this(ClassLoaderUtils.getDefaultClassLoader(), commonLoggerFactory);
    }

    public DefaultTraceMetadataLoaderService(URL[] jarLists, CommonLoggerFactory commonLoggerFactory) {
        if (jarLists == null) {
            throw new NullPointerException("jarLists must not be null");
        }
        this.loader = new TraceMetadataLoader(commonLoggerFactory);
        loader.load(jarLists);

    }

    public DefaultTraceMetadataLoaderService(List<TraceMetadataProvider> providers, CommonLoggerFactory commonLoggerFactory) {
        if (providers == null) {
            throw new NullPointerException("providers must not be null");
        }
        this.loader = new TraceMetadataLoader();
        loader.load(providers);

    }


    public DefaultTraceMetadataLoaderService(ClassLoader classLoader, CommonLoggerFactory commonLoggerFactory) {
        if (classLoader == null) {
            throw new NullPointerException("classLoader must not be null");
        }
        this.loader = new TraceMetadataLoader(commonLoggerFactory);
        loader.load(classLoader);
    }

    @Override
    public List<ServiceTypeInfo> getServiceTypeInfos() {
        return loader.getServiceTypeInfos();
    }

    @Override
    public List<AnnotationKey> getAnnotationKeys() {
        return loader.getAnnotationKeys();
    }


}
