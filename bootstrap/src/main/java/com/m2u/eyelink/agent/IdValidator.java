package com.m2u.eyelink.agent;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.m2u.eyelink.logging.ELLogger;
import com.m2u.eyelink.util.ByteUtils;

public class IdValidator {
	private static final ELLogger logger = ELLogger.getLogger(ELAgent.class
			.getName());

    private static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z0-9\\._\\-]+");

    private final Properties property;
    private static final int MAX_ID_LENGTH = 24;
    
    public IdValidator() {
        this(System.getProperties());
    }

    public IdValidator(Properties property) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        this.property = property;
    }
    
    private String getValidId(String propertyName, int maxSize) {
        logger.info("check -D" + propertyName);
        String value = property.getProperty(propertyName);
        if (value == null){
            logger.warn("-D" + propertyName + " is null. value:null");
            return null;
        }
        // blanks not permitted around value
        value = value.trim();
        if (value.isEmpty()) {
            logger.warn("-D" + propertyName + " is empty. value:''");
            return null;
        }

        if (!validateId(value, maxSize)) {
            logger.warn("invalid Id. " + propertyName + " can only contain [a-zA-Z0-9], '.', '-', '_'. maxLength:" + maxSize + " value:" + value);
            return null;
        }

        if (logger.isInfoEnabled()) {
            logger.info("check success. -D" + propertyName + ":" + value + " length:" + getLength(value));
        }
        return value;
    }
    
    public boolean validateId(String id, int maxLength) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        if (maxLength <= 0) {
            throw new IllegalArgumentException("negative maxLength:" + maxLength);
        }

        if (!checkPattern(id)) {
            return false;
        }
        if (!checkLength(id, maxLength)) {
            return false;
        }

        return true;
    }
    
    public boolean checkPattern(String id) {
        final Matcher matcher = ID_PATTERN.matcher(id);
        return matcher.matches();
    }
    
    public boolean checkLength(String id, int maxLength) {
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        // try encode
        final int idLength = getLength(id);
        if (idLength <= 0) {
            return false;
        }
        return idLength <= maxLength;
    }
    
    public static int getLength(String id) {
        if (id == null) {
            return -1;
        }

        final byte[] idBytes = ByteUtils.toBytes(id);
        if (idBytes == null) {
            // encoding fail
            return -1;
        }
        return idBytes.length;
    }


    public String getApplicationName() {
        return this.getValidId("eyelink.applicationName", MAX_ID_LENGTH);
    }

    public String getAgentId() {
        return this.getValidId("eyelink.agentId", MAX_ID_LENGTH);
    }
}
