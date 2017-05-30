package com.m2u.eyelink.context.thrift;

import org.apache.thrift.TBase;

public enum TCommandType {

    // Using reflection would make code cleaner.
    // But it also makes it hard to handle exception, constructor and will show relatively low performance.

    RESULT((short) 320, TResult.class) {
        @Override
        public TBase newObject() {
            return new TResult();
        }
    },
    TRANSFER((short) 700, TCommandTransfer.class) {
        @Override
        public TBase newObject() {
            return new TCommandTransfer();
        }
    },
    TRANSFER_RESPONSE((short) 701, TCommandTransferResponse.class) {
        @Override
        public TBase newObject() {
            return new TCommandTransferResponse();
        }
    },
    ECHO((short) 710, TCommandEcho.class) {
        @Override
        public TBase newObject() {
            return new TCommandEcho();
        }
    },
    THREAD_DUMP((short) 720, TCommandThreadDump.class) {
        @Override
        public TBase newObject() {
            return new TCommandThreadDump();
        }
    },
    THREAD_DUMP_RESPONSE((short) 721, TCommandThreadDumpResponse.class) {
        @Override
        public TBase newObject() {
            return new TCommandThreadDumpResponse();
        }
    },
    ACTIVE_THREAD_COUNT((short) 730, TCmdActiveThreadCount.class) {
        @Override
        public TBase newObject() {
            return new TCmdActiveThreadCount();
        }
    },
    ACTIVE_THREAD_COUNT_RESPONSE((short) 731, TCmdActiveThreadCountRes.class) {
        @Override
        public TBase newObject() {
            return new TCmdActiveThreadCountRes();
        }
    },
    ACTIVE_THREAD_DUMP((short) 740, TCmdActiveThreadDump.class) {
        @Override
        public TBase newObject() {
            return new TCmdActiveThreadDump();
        }
    },
    ACTIVE_THREAD_DUMP_RESPONSE((short) 741, TCmdActiveThreadDumpRes.class) {
        @Override
        public TBase newObject() {
            return new TCmdActiveThreadDumpRes();
        }
    },
    ACTIVE_THREAD_LIGHT_DUMP((short) 750, TCmdActiveThreadLightDump.class) {
        @Override
        public TBase newObject() {
            return new TCmdActiveThreadLightDump();
        }
    },
    ACTIVE_THREAD_LIGHT_DUMP_RESPONSE((short) 751, TCmdActiveThreadLightDumpRes.class) {
        @Override
        public TBase newObject() {
            return new TCmdActiveThreadLightDumpRes();
        }
    };

    private final short code;
    private final Class<? extends TBase> clazz;
    private final Header header;

    private TCommandType(short code, Class<? extends TBase> clazz) {
        this.code = code;
        this.clazz = clazz;
        this.header = createHeader(code);
    }

    public short getCode() {
        return code;
    }

    public Class getClazz() {
        return clazz;
    }

    protected boolean isInstanceOf(Object value) {
        return this.clazz.isInstance(value);
    }

    protected Header getHeader() {
        return header;
    }

    public abstract TBase newObject();

    private static Header createHeader(short code) {
        Header header = new Header();
        header.setType(code);
        return header;
    }

    public static TCommandType getType(Class<? extends TBase> clazz) {
        TCommandType[] commandTypes = values();
        for (TCommandType commandType : commandTypes) {
            if (commandType.getClazz() == clazz) {
                return commandType;
            }
        }

        return null;
    }

    public static TCommandType getType(short code) {
        TCommandType[] commandTypes = values();
        for (TCommandType commandType : commandTypes) {
            if (commandType.getCode() == code) {
                return commandType;
            }
        }

        return null;
    }

}
