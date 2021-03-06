package com.m2u.eyelink.context;

import org.apache.thrift.TBase;

import com.m2u.eyelink.context.thrift.TAnnotation;
import com.m2u.eyelink.util.StringUtils;

public final class AnnotationValueMapper {
    private AnnotationValueMapper() {
    }

    public static void mappingValue(TAnnotation annotation, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            annotation.setValue(TAnnotationValue.stringValue((String) value));
            return;
        } else if (value instanceof Integer) {
            annotation.setValue(TAnnotationValue.intValue((Integer) value));
            return;
        } else if (value instanceof Long) {
            annotation.setValue(TAnnotationValue.longValue((Long) value));
            return;
        } else if (value instanceof Boolean) {
            annotation.setValue(TAnnotationValue.boolValue((Boolean) value));
            return;
        } else if (value instanceof Byte) {
            annotation.setValue(TAnnotationValue.byteValue((Byte) value));
            return;
        } else if (value instanceof Float) {
            // thrift does not contain "float" type
            annotation.setValue(TAnnotationValue.doubleValue((Float) value));
            return;
        } else if (value instanceof Double) {
            annotation.setValue(TAnnotationValue.doubleValue((Double) value));
            return;
        } else if (value instanceof byte[]) {
            annotation.setValue(TAnnotationValue.binaryValue((byte[]) value));
            return;
        } else if (value instanceof Short) {
            annotation.setValue(TAnnotationValue.shortValue((Short) value));
            return;
        } else if (value instanceof TBase) {
            throw new IllegalArgumentException("TBase not supported. Class:" + value.getClass());
        }
        String str = StringUtils.abbreviate(value.toString());
        annotation.setValue(TAnnotationValue.stringValue(str));
    }

}
