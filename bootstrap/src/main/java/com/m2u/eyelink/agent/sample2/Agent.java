package com.m2u.eyelink.agent.sample2;

import java.lang.instrument.Instrumentation;

/**
 * @author k
 *
 */
public class Agent {
	public static void premain(String args, Instrumentation inst) {
		inst.addTransformer(new JdbcQueryTransformer());
	}

	public static void agentmain(String args, Instrumentation inst) {
		inst.addTransformer(new JdbcQueryTransformer());
	}
}