package com.m2u.eyelink.agent.profiler.context.storage;

import com.m2u.eyelink.context.Storage;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.sender.DataSender;

public class SpanStorageFactory implements StorageFactory {

    protected final DataSender dataSender;

    public SpanStorageFactory(DataSender dataSender) {
        if (dataSender == null) {
            throw new NullPointerException("dataSender must not be null");
        }
        this.dataSender = dataSender;
    }

    @Override
    public Storage createStorage() {
        return new SpanStorage(this.dataSender);
    }
}
