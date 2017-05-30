package com.m2u.eyelink.agent.resolver.condition;

import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.util.SimpleProperty;
import com.m2u.eyelink.util.SystemProperty;

public class PropertyCondition implements Condition<String>, ConditionValue<SimpleProperty> {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass().getName()); 

    private final SimpleProperty property;
    
    public PropertyCondition() {
        this(SystemProperty.INSTANCE);
    }

    public PropertyCondition(SimpleProperty property) {
        this.property = property;
    }
    
    /**
     * Checks if the specified value is in <tt>SimpleProperty</tt>.
     * 
     * @param requiredKey the values to check if they exist in <tt>SimpleProperty</tt>
     * @return <tt>true</tt> if the specified key is in <tt>SimpleProperty</tt>; 
     *         <tt>false</tt> if otherwise, or if <tt>null</tt> or empty key is provided
     */
    @Override
    public boolean check(String requiredKey) {
        if (requiredKey == null || requiredKey.isEmpty()) {
            return false;
        }
        if (this.property.getProperty(requiredKey) != null) {
            logger.debug("Property '{}' found in [{}]", requiredKey, this.property.getClass().getSimpleName());
            return true;
        } else {
            logger.debug("Property '{}' not found in [{}]", requiredKey, this.property.getClass().getSimpleName());
            return false;
        }
    }
    
    /**
     * Returns the <tt>SimpleProperty</tt>.
     * 
     * @return the {@link SimpleProperty} instance
     */
    @Override
    public SimpleProperty getValue() {
        return this.property;
    }
    
}
