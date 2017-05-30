package com.m2u.eyelink.agent.profiler.receiver.service;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.profiler.receiver.ProfilerRequestCommandService;
import com.m2u.eyelink.context.thrift.TCommandEcho;

public class EchoService implements ProfilerRequestCommandService  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public TBase<?, ?> requestCommandService(TBase tbase) {
        logger.info("{} execute {}.", this, tbase);

        TCommandEcho param = (TCommandEcho) tbase;
        return param;
    }

    @Override
    public Class<? extends TBase> getCommandClazz() {
        return TCommandEcho.class;
    }

}
