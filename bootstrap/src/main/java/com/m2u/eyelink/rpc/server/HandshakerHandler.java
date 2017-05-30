package com.m2u.eyelink.rpc.server;

import java.util.Map;

import com.m2u.eyelink.rpc.packet.HandshakeResponseCode;

public interface HandshakerHandler {
	   HandshakeResponseCode handleHandshake(Map properties);

}
