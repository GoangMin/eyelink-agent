package com.m2u.eyelink.sender;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum StreamCode {
	   // Status Code
    OK((short) 0),

    UNKNWON_ERROR((short) 100),

    ID_ERROR((short) 110),
    ID_ILLEGAL((short) 111),
    ID_DUPLICATED((short) 112),
    ID_NOT_FOUND((short) 113),

    STATE_ERROR((short) 120),
    STATE_NOT_CONNECTED((short) 121),
    STATE_CLOSED((short) 122),

    TYPE_ERROR((short) 130),
    TYPE_UNKNOWN((short) 131),
    TYPE_UNSUPPORT((short) 132),

    PACKET_ERROR((short) 140),
    PACKET_UNKNOWN((short) 141),
    PACKET_UNSUPPORT((short) 142),

    CONNECTION_ERRROR((short) 150),
    CONNECTION_NOT_FOUND((short) 151),
    CONNECTION_TIMEOUT((short) 152),
    CONNECTION_UNSUPPORT((short) 153),

    ROUTE_ERROR((short)160);

    private final short value;
    private final static Map<Short, StreamCode> CODE_MAP = Collections.unmodifiableMap(initializeCodeMapping());

    StreamCode(short value) {
        this.value = value;
    }

    public static StreamCode getCode(short value) {
        StreamCode streamCode = CODE_MAP.get(value);
        if (streamCode != null) {
            return streamCode;
        }

        short codeGroup = (short) (value - (value % 10));
        streamCode = CODE_MAP.get(codeGroup);
        if (streamCode != null) {
            return streamCode;
        }

        return UNKNWON_ERROR;
    }

    private static Map<Short, StreamCode> initializeCodeMapping() {
        Map<Short, StreamCode> codeMap = new HashMap<Short, StreamCode>();
        for (StreamCode streamCode : StreamCode.values()) {
            codeMap.put(streamCode.value, streamCode);
        }
        return codeMap;
    }

    public short value() {
        return value;
    }

}
