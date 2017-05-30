package com.m2u.eyelink.agent.resolver.condition;

import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;


public class ClassResourceCondition implements Condition<String> {

    private static final String CLASS_EXTENSION = ".class";

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass().getName()); 

    private String getClassNameAsResource(String className) {
        String classNameAsResource = className.replace('.', '/');
        return classNameAsResource.endsWith(CLASS_EXTENSION) ? classNameAsResource : classNameAsResource.concat(CLASS_EXTENSION);
    }
    
    /**
     * Checks if the specified class can be found in the current System ClassLoader's search path.
     * 
     * @param requiredClass the fully qualified class name of the class to check
     * @return <tt>true</tt> if the specified class can be found in the system class loader's search path, 
     *         <tt>false</tt> if otherwise
     */
    @Override
    public boolean check(String requiredClass) {
        if (requiredClass == null || requiredClass.isEmpty()) {
            return false;
        }
        String classNameAsResource = getClassNameAsResource(requiredClass);
        if (ClassLoader.getSystemResource(classNameAsResource) != null) {
            logger.debug("Resource found - [{}]", classNameAsResource);
            return true;
        } else {
            logger.debug("Resource not found - [{}]", classNameAsResource);
            return false;
        }
    }

}
