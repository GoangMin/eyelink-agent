package com.m2u.eyelink.agent.profiler.interceptor.factory;

import com.m2u.eyelink.agent.instrument.InstrumentClass;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.InstrumentMethod;
import com.m2u.eyelink.agent.interceptor.ApiIdAwareAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor0;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor1;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor2;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor3;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor4;
import com.m2u.eyelink.agent.interceptor.AroundInterceptor5;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleApiIdAwareAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor0;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor1;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor2;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor3;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor4;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleAroundInterceptor5;
import com.m2u.eyelink.agent.interceptor.ExceptionHandleStaticAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.Interceptor;
import com.m2u.eyelink.agent.interceptor.StaticAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.annotation.Scope;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedApiIdAwareAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor0;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor1;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor2;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor3;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor4;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedInterceptor5;
import com.m2u.eyelink.agent.interceptor.scope.ExceptionHandleScopedStaticAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.scope.ExecutionPolicy;
import com.m2u.eyelink.agent.interceptor.scope.InterceptorScope;
import com.m2u.eyelink.agent.interceptor.scope.ScopedApiIdAwareAroundInterceptor;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor0;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor1;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor2;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor3;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor4;
import com.m2u.eyelink.agent.interceptor.scope.ScopedInterceptor5;
import com.m2u.eyelink.agent.interceptor.scope.ScopedStaticAroundInterceptor;
import com.m2u.eyelink.agent.plugin.ObjectFactory;
import com.m2u.eyelink.agent.profiler.objectfactory.AutoBindingObjectFactory;
import com.m2u.eyelink.agent.profiler.objectfactory.InterceptorArgumentProvider;

public class AnnotatedInterceptorFactory implements InterceptorFactory {
    private final InstrumentContext pluginContext;
    private final boolean exceptionHandle;

    public AnnotatedInterceptorFactory(InstrumentContext pluginContext) {
        this(pluginContext, false);
    }

    public AnnotatedInterceptorFactory(InstrumentContext pluginContext, boolean exceptionHandle) {
        this.pluginContext = pluginContext;
        this.exceptionHandle = exceptionHandle;
    }

    @Override
    public Interceptor getInterceptor(ClassLoader classLoader, String interceptorClassName, Object[] providedArguments, InterceptorScope scope, ExecutionPolicy policy, InstrumentClass target, InstrumentMethod targetMethod) {
        Class<? extends Interceptor> interceptorType = pluginContext.injectClass(classLoader, interceptorClassName);

        if (scope == null) {
            Scope interceptorScope = interceptorType.getAnnotation(Scope.class);

            if (interceptorScope != null) {
                String scopeName = interceptorScope.value();
                scope = pluginContext.getInterceptorScope(scopeName);
                policy = interceptorScope.executionPolicy();
            }
        }

        AutoBindingObjectFactory factory = new AutoBindingObjectFactory(pluginContext, classLoader);
        ObjectFactory objectFactory = ObjectFactory.byConstructor(interceptorClassName, providedArguments);
        InterceptorArgumentProvider interceptorArgumentProvider = new InterceptorArgumentProvider(pluginContext.getTraceContext(), scope, target, targetMethod);

        Interceptor interceptor = (Interceptor) factory.createInstance(objectFactory, interceptorArgumentProvider);

        if (scope != null) {
            if (exceptionHandle) {
                interceptor = wrapByExceptionHandleScope(interceptor, scope, policy == null ? ExecutionPolicy.BOUNDARY : policy);
            } else {
                interceptor = wrapByScope(interceptor, scope, policy == null ? ExecutionPolicy.BOUNDARY : policy);
            }
        } else {
            if (exceptionHandle) {
                interceptor = wrapByExceptionHandle(interceptor);
            }
        }

        return interceptor;
    }

    private Interceptor wrapByScope(Interceptor interceptor, InterceptorScope scope, ExecutionPolicy policy) {
        if (interceptor instanceof AroundInterceptor) {
            return new ScopedInterceptor((AroundInterceptor) interceptor, scope, policy);
        } else if (interceptor instanceof StaticAroundInterceptor) {
            return new ScopedStaticAroundInterceptor((StaticAroundInterceptor) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor5) {
            return new ScopedInterceptor5((AroundInterceptor5) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor4) {
            return new ScopedInterceptor4((AroundInterceptor4) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor3) {
            return new ScopedInterceptor3((AroundInterceptor3) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor2) {
            return new ScopedInterceptor2((AroundInterceptor2) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor1) {
            return new ScopedInterceptor1((AroundInterceptor1) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor0) {
            return new ScopedInterceptor0((AroundInterceptor0) interceptor, scope, policy);
        } else if (interceptor instanceof ApiIdAwareAroundInterceptor) {
            return new ScopedApiIdAwareAroundInterceptor((ApiIdAwareAroundInterceptor) interceptor, scope, policy);
        }

        throw new IllegalArgumentException("Unexpected interceptor type: " + interceptor.getClass());
    }

    private Interceptor wrapByExceptionHandleScope(Interceptor interceptor, InterceptorScope scope, ExecutionPolicy policy) {
        if (interceptor instanceof AroundInterceptor) {
            return new ExceptionHandleScopedInterceptor((AroundInterceptor) interceptor, scope, policy);
        } else if (interceptor instanceof StaticAroundInterceptor) {
            return new ExceptionHandleScopedStaticAroundInterceptor((StaticAroundInterceptor) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor5) {
            return new ExceptionHandleScopedInterceptor5((AroundInterceptor5) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor4) {
            return new ExceptionHandleScopedInterceptor4((AroundInterceptor4) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor3) {
            return new ExceptionHandleScopedInterceptor3((AroundInterceptor3) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor2) {
            return new ExceptionHandleScopedInterceptor2((AroundInterceptor2) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor1) {
            return new ExceptionHandleScopedInterceptor1((AroundInterceptor1) interceptor, scope, policy);
        } else if (interceptor instanceof AroundInterceptor0) {
            return new ExceptionHandleScopedInterceptor0((AroundInterceptor0) interceptor, scope, policy);
        } else if (interceptor instanceof ApiIdAwareAroundInterceptor) {
            return new ExceptionHandleScopedApiIdAwareAroundInterceptor((ApiIdAwareAroundInterceptor) interceptor, scope, policy);
        }

        throw new IllegalArgumentException("Unexpected interceptor type: " + interceptor.getClass());
    }

    private Interceptor wrapByExceptionHandle(Interceptor interceptor) {
        if (interceptor instanceof AroundInterceptor) {
            return new ExceptionHandleAroundInterceptor((AroundInterceptor) interceptor);
        } else if (interceptor instanceof StaticAroundInterceptor) {
            return new ExceptionHandleStaticAroundInterceptor((StaticAroundInterceptor) interceptor);
        } else if (interceptor instanceof AroundInterceptor5) {
            return new ExceptionHandleAroundInterceptor5((AroundInterceptor5) interceptor);
        } else if (interceptor instanceof AroundInterceptor4) {
            return new ExceptionHandleAroundInterceptor4((AroundInterceptor4) interceptor);
        } else if (interceptor instanceof AroundInterceptor3) {
            return new ExceptionHandleAroundInterceptor3((AroundInterceptor3) interceptor);
        } else if (interceptor instanceof AroundInterceptor2) {
            return new ExceptionHandleAroundInterceptor2((AroundInterceptor2) interceptor);
        } else if (interceptor instanceof AroundInterceptor1) {
            return new ExceptionHandleAroundInterceptor1((AroundInterceptor1) interceptor);
        } else if (interceptor instanceof AroundInterceptor0) {
            return new ExceptionHandleAroundInterceptor0((AroundInterceptor0) interceptor);
        } else if (interceptor instanceof ApiIdAwareAroundInterceptor) {
            return new ExceptionHandleApiIdAwareAroundInterceptor((ApiIdAwareAroundInterceptor) interceptor);
        }

        throw new IllegalArgumentException("Unexpected interceptor type: " + interceptor.getClass());
    }
}
