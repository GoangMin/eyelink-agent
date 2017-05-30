package com.m2u.eyelink.rpc.client;

import java.util.Map;

import com.m2u.eyelink.rpc.control.ControlMessageDecoder;
import com.m2u.eyelink.rpc.control.ControlMessageEncoder;
import com.m2u.eyelink.rpc.control.ProtocolException;

public final class ControlMessageEncodingUtils {

    private static final ControlMessageEncoder encoder = new ControlMessageEncoder();
    private static final ControlMessageDecoder decoder = new ControlMessageDecoder();

    private ControlMessageEncodingUtils() {
    }

    public static byte[] encode(Map<String, Object> value) throws ProtocolException {
        return encoder.encode(value);
    }

    public static Object decode(byte[] in) throws ProtocolException {
        return decoder.decode(in);
    }

}
