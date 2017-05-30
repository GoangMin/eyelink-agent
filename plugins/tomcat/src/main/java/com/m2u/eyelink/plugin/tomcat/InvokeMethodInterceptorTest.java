package com.m2u.eyelink.plugin.tomcat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.context.DefaultMethodDescriptor;
import com.m2u.eyelink.context.Header;
import com.m2u.eyelink.context.MethodDescriptor;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.logging.Slf4jLoggerBinder;
import com.m2u.eyelink.plugin.tomcat.interceptor.StandardHostValveInvokeInterceptor;

public class InvokeMethodInterceptorTest {
	private static final Logger logger = LoggerFactory
			.getLogger(InvokeMethodInterceptorTest.class.getName());
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private final MethodDescriptor descriptor = new DefaultMethodDescriptor(
			"org.apache.catalina.core.StandardHostValve", "invoke",
			new String[] { "org.apache.catalina.connector.Request",
					"org.apache.catalina.connector.Response" }, new String[] {
					"request", "response" });

	@BeforeClass
	public static void before() {
		 PLoggerFactory.initialize(new Slf4jLoggerBinder());
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	private TraceContext spyTraceContext() {
		// return new MockTraceContext();
		MockTraceContextFactory traceContextFactory = new MockTraceContextFactory();
		TraceContext traceContext = traceContextFactory.create();
		return spy(traceContext);
	}

	@Test
	public void testHeaderNOTExists() {

		when(request.getRequestURI()).thenReturn("/spring-mvc-showcase");
		when(request.getRemoteAddr()).thenReturn("10.0.0.1");
		when(request.getHeader(Header.HTTP_TRACE_ID.toString())).thenReturn(
				null);
		when(request.getHeader(Header.HTTP_PARENT_SPAN_ID.toString()))
				.thenReturn(null);
		when(request.getHeader(Header.HTTP_SPAN_ID.toString()))
				.thenReturn(null);
		when(request.getHeader(Header.HTTP_SAMPLED.toString()))
				.thenReturn(null);
		when(request.getHeader(Header.HTTP_FLAGS.toString())).thenReturn(null);
		Enumeration<?> enumeration = mock(Enumeration.class);
		when(request.getParameterNames()).thenReturn(
				(Enumeration<String>) enumeration);

		TraceContext traceContext = spyTraceContext();
		StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(
				traceContext, descriptor);

		interceptor.before("target", new Object[] { request, response });
		interceptor.after("target", new Object[] { request, response },
				new Object(), null);

		verify(traceContext, times(1)).newTraceObject();

		interceptor.before("target", new Object[] { request, response });
		interceptor.after("target", new Object[] { request, response },
				new Object(), null);

		verify(traceContext, times(2)).newTraceObject();
	}

}
