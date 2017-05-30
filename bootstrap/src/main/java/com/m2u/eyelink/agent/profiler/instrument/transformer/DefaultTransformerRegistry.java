package com.m2u.eyelink.agent.profiler.instrument.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.m2u.eyelink.agent.instrument.matcher.ClassNameMatcher;
import com.m2u.eyelink.agent.instrument.matcher.Matcher;
import com.m2u.eyelink.agent.instrument.matcher.MultiClassNameMatcher;
import com.m2u.eyelink.agent.instrument.transformer.TransformerRegistry;
import com.m2u.eyelink.agent.profiler.JavaAssistUtils;

public class DefaultTransformerRegistry implements TransformerRegistry {

    // No concurrent issue because only one thread put entries to the map and get operations are started AFTER the map is completely build.
    // Set the map size big intentionally to keep hash collision low.
    private final Map<String, ClassFileTransformer> registry = new HashMap<String, ClassFileTransformer>(512);

    @Override
    public ClassFileTransformer findTransformer(String className) {
        return registry.get(className);
    }
    
    public void addTransformer(Matcher matcher, ClassFileTransformer transformer) {
        // TODO extract matcher process
        if (matcher instanceof ClassNameMatcher) {
            final ClassNameMatcher classNameMatcher = (ClassNameMatcher)matcher;
            String className = classNameMatcher.getClassName();
            addModifier0(transformer, className);
        } else if (matcher instanceof MultiClassNameMatcher) {
            final MultiClassNameMatcher classNameMatcher = (MultiClassNameMatcher)matcher;
            List<String> classNameList = classNameMatcher.getClassNames();
            for (String className : classNameList) {
                addModifier0(transformer, className);
            }
        } else {
            throw new IllegalArgumentException("unsupported matcher :" + matcher);
        }
    }

    private void addModifier0(ClassFileTransformer transformer, String className) {
        final String classInternalName = JavaAssistUtils.javaNameToJvmName(className);
        ClassFileTransformer old = registry.put(classInternalName, transformer);
        
        if (old != null) {
            throw new IllegalStateException("Transformer already exist. className:" + classInternalName + " new:" + transformer.getClass() + " old:" + old.getClass());
        }
    }
}
