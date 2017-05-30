package com.m2u.eyelink.rpc.util;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.rpc.ELAgentSocketException;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;

public final class ClientFactoryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactoryUtils.class);

    public static ELAgentClient createELAgentClient(String host, int port, ELAgentClientFactory clientFactory) {
        InetSocketAddress connectAddress = new InetSocketAddress(host, port);
        return createELAgentClient(connectAddress, clientFactory);
    }

    public static ELAgentClient createELAgentClient(InetSocketAddress connectAddress, ELAgentClientFactory clientFactory) {
    	ELAgentClient pinpointClient = null;
        for (int i = 0; i < 3; i++) {
            try {
                pinpointClient = clientFactory.connect(connectAddress);
                LOGGER.info("tcp connect success. remote:{}", connectAddress);
                return pinpointClient;
            } catch (ELAgentSocketException e) {
                LOGGER.warn("tcp connect fail. remote:{} try reconnect, retryCount:{}", connectAddress, i);
            }
        }
        LOGGER.warn("change background tcp connect mode remote:{} ", connectAddress);
        pinpointClient = clientFactory.scheduledConnect(connectAddress);

        return pinpointClient;
    }

}
