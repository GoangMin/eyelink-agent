package com.m2u.eyelink.plugin.tomcat;

import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.DefaultTraceContext;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.sender.EnhancedDataSender;
import com.m2u.eyelink.sender.LoggingDataSender;
import com.m2u.eyelink.test.TestAgentInformation;

public class MockTraceContextFactory {
	private EnhancedDataSender priorityDataSender = new LoggingDataSender();

	public void setPriorityDataSender(EnhancedDataSender priorityDataSender) {
		if (priorityDataSender == null) {
			throw new NullPointerException(
					"priorityDataSender must not be null");
		}
		this.priorityDataSender = priorityDataSender;
	}

	public TraceContext create() {
		DefaultTraceContext traceContext = new DefaultTraceContext(
				new TestAgentInformation());
		ProfilerConfig profilerConfig = new DefaultProfilerConfig();
		traceContext.setProfilerConfig(profilerConfig);

		traceContext.setPriorityDataSender(priorityDataSender);

		return traceContext;
	}
}
