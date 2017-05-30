package com.m2u.eyelink.common.service;

import com.m2u.eyelink.trace.AnnotationKey;


public interface AnnotationKeyRegistryService {
    AnnotationKey findAnnotationKey(int annotationCode);

    AnnotationKey findAnnotationKeyByName(String keyName);

    AnnotationKey findApiErrorCode(int annotationCode);
}
