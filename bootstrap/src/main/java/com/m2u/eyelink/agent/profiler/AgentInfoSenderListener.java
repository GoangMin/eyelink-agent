package com.m2u.eyelink.agent.profiler;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.context.thrift.HeaderTBaseDeserializerFactory;
import com.m2u.eyelink.context.thrift.SerializationUtils;
import com.m2u.eyelink.context.thrift.TResult;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.ResponseMessage;
import com.m2u.eyelink.sender.FutureListener;

public class AgentInfoSenderListener implements FutureListener<ResponseMessage> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final AtomicBoolean isSuccessful;

	public AgentInfoSenderListener(AtomicBoolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	@Override
	public void onComplete(Future<ResponseMessage> future) {
		try {
			if (future != null && future.isSuccess()) {
				TBase<?, ?> tbase = deserialize(future);
				if (tbase instanceof TResult) {
					TResult result = (TResult) tbase;
					if (result.isSuccess()) {
						logger.debug("result success");
						this.isSuccessful.set(true);
						return;
					} else {
						logger.warn("request fail. Caused:{}",
								result.getMessage());
					}
				} else {
					logger.warn("Invalid Class. {}", tbase);
				}
			}
		} catch (Exception e) {
			logger.warn("request fail. caused:{}", e.getMessage());
		}
	}

	private TBase<?, ?> deserialize(Future<ResponseMessage> future) {
		final ResponseMessage responseMessage = future.getResult();

		// TODO Should we change this to thread local cache? This object's life
		// cycle is different because it could be created many times.
		// Should we cache this?
		byte[] message = responseMessage.getMessage();
		return SerializationUtils.deserialize(message,
				HeaderTBaseDeserializerFactory.DEFAULT_FACTORY, null);

	}

}
