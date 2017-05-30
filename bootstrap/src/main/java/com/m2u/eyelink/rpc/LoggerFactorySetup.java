package com.m2u.eyelink.rpc;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;


public class LoggerFactorySetup {

    public static final Slf4JLoggerFactory LOGGER_FACTORY = new Slf4JLoggerFactory();

    public static void setupSlf4jLoggerFactory() {
        InternalLoggerFactory.setDefaultFactory(LOGGER_FACTORY);
    }
}
