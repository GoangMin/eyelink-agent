package com.m2u.eyelink.sender;

import com.m2u.eyelink.rpc.client.ELAgentClient;

public interface ELAgentClientReconnectEventListener {
	/*
	 * there is no event except "reconnect" currently. when additional events
	 * are needed, it will be useful to pass with Event
	 */
	void reconnectPerformed(ELAgentClient client);
}
