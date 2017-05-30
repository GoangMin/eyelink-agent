package com.m2u.eyelink.agent.interceptor;

import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;

public class InterceptorInvokerHelper {
    private static boolean propagateException = false;
    private static final PLogger logger = PLoggerFactory.getLogger(InterceptorInvokerHelper.class.getName());
    
    public static void handleException(Throwable t) {
        if (propagateException) {
            throw new RuntimeException(t);
        } else {
            logger.warn("Exception occurred from interceptor", t);
        }
    }
    
    public static void setPropagateException(boolean propagate) {
        propagateException = propagate;
    }
}
