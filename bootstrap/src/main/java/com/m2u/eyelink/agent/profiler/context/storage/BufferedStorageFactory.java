package com.m2u.eyelink.agent.profiler.context.storage;

import com.m2u.eyelink.agent.profiler.context.SpanChunkFactory;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.Storage;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.sender.DataSender;

public class BufferedStorageFactory implements StorageFactory {

    private final DataSender dataSender;
    private final int bufferSize;
    private final SpanChunkFactory spanChunkFactory;

    public BufferedStorageFactory(DataSender dataSender, ProfilerConfig config, AgentInformation agentInformation) {
        if (dataSender == null) {
            throw new NullPointerException("dataSender must not be null");
        }
        if (config == null) {
            throw new NullPointerException("config must not be null");
        }
        this.dataSender = dataSender;

        this.bufferSize = config.getIoBufferingBufferSize();

        this.spanChunkFactory = new SpanChunkFactory(agentInformation);
    }


    @Override
    public Storage createStorage() {
        BufferedStorage bufferedStorage = new BufferedStorage(this.dataSender, spanChunkFactory, this.bufferSize);
        return bufferedStorage;
    }

    @Override
    public String toString() {
        return "BufferedStorageFactory{" +
                "bufferSize=" + bufferSize +
                ", dataSender=" + dataSender +
                '}';
    }
}
