package com.m2u.eyelink.agent.profiler.objectfactory;

import java.lang.annotation.Annotation;

import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.Instrumentor;
import com.m2u.eyelink.agent.instrument.InstrumentorDelegate;
import com.m2u.eyelink.agent.interceptor.annotation.Name;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.agent.profiler.util.TypeUtils;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.exception.ELAgentException;


public class ProfilerPluginArgumentProvider implements ArgumentProvider {
    private final InstrumentContext pluginContext;

    public ProfilerPluginArgumentProvider(InstrumentContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    @Override
    public Option get(int index, Class<?> type, Annotation[] annotations) {
        if (type == Trace.class) {
            return Option.withValue(pluginContext.getTraceContext().currentTraceObject());
        } else if (type == TraceContext.class) {
            return Option.withValue(pluginContext.getTraceContext());
        } else if (type == Instrumentor.class) {
            final InstrumentorDelegate delegate = new InstrumentorDelegate(pluginContext);
            return Option.withValue(delegate);
        } else if (type == InterceptorScope.class) {
            Name annotation = TypeUtils.findAnnotation(annotations, Name.class);
            
            if (annotation == null) {
                return Option.empty();
            }
            
            InterceptorScope scope = pluginContext.getInterceptorScope(annotation.value());
            
            if (scope == null) {
                throw new ELAgentException("No such Scope: " + annotation.value());
            }
            
            return Option.withValue(scope);
        }
        
        return Option.empty();
    }
}
