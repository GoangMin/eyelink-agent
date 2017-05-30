package com.m2u.eyelink.agent.resolver.condition;

import java.io.IOException;
import java.util.jar.JarFile;

import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.util.SimpleProperty;
import com.m2u.eyelink.util.SystemProperty;
import com.m2u.eyelink.util.SystemPropertyKey;

public class MainClassCondition implements Condition<String>, ConditionValue<String> {

    private static final String MANIFEST_MAIN_CLASS_KEY = "Main-Class";
    private static final String NOT_FOUND = null;

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass().getName()); 

    private final String applicationMainClassName;

    public MainClassCondition() {
        this(SystemProperty.INSTANCE);
    }

    public MainClassCondition(SimpleProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("properties should not be null");
        }
        this.applicationMainClassName = getMainClassName(property);
    }

    /**
     * Checks if the specified value matches the fully qualified class name of the application's main class.
     * If the main class cannot be resolved, the method return <tt>false</tt>.
     * 
     * @param condition the value to check against the application's main class name
     * @return <tt>true</tt> if the specified value matches the name of the main class; 
     *         <tt>false</tt> if otherwise, or if the main class cannot be resolved
     */
    @Override
    public boolean check(String condition) {
        if (this.applicationMainClassName == NOT_FOUND) {
            return false;
        }
        if (this.applicationMainClassName.equals(condition)) {
            logger.debug("Main class match - [{}]", this.applicationMainClassName, condition);
            return true;
        } else {
            logger.debug("Main class does not match - found : [{}], expected : [{}]", this.applicationMainClassName, condition);
            return false;
        }
    }
    
    /**
     * Returns the fully qualified class name of the application's main class.
     * 
     * @return the fully qualified class name of the main class, or an empty string if the main class cannot be resolved
     */
    @Override
    public String getValue() {
        if (this.applicationMainClassName == NOT_FOUND) {
            return "";
        }
        return this.applicationMainClassName;
    }

    private String getMainClassName(SimpleProperty property) {
        String javaCommand = property.getProperty(SystemPropertyKey.SUN_JAVA_COMMAND.getKey(), "").split(" ")[0];
        if (javaCommand.isEmpty()) {
            logger.warn("Error retrieving main class from [{}]", property.getClass().getName());
            return NOT_FOUND;
        } else {
            JarFile executableArchive = null;
            try {
                executableArchive = new JarFile(javaCommand);
                return extractMainClassFromArchive(executableArchive);
            } catch (IOException e) {
                // If it's not a valid java archive, VM shouldn't start in the first place.
                // Thus this would simply be a main class
                return javaCommand;
            } catch (Exception e) {
                // fail-safe, application shouldn't not start because of this
                logger.warn("Error retrieving main class from java command : [{}]", javaCommand, e);
                return NOT_FOUND;
            } finally {
                if (executableArchive != null) {
                    try {
                        executableArchive.close();
                    } catch (IOException e) {
                        logger.warn("Error closing jarFile : [{}]", executableArchive.getName(), e);
                    }
                }
            }
        }
    }

    private String extractMainClassFromArchive(JarFile bootstrapJar) throws IOException {
        String mainClassFromManifest = bootstrapJar.getManifest().getMainAttributes().getValue(MANIFEST_MAIN_CLASS_KEY);
        if (mainClassFromManifest == null) {
            return NOT_FOUND;
        }
        return mainClassFromManifest;
    }

}
