package com.m2u.eyelink.sender;

import org.apache.thrift.TBase;

import com.m2u.eyelink.rpc.ResponseMessage;

public interface EnhancedDataSender extends DataSender {
    boolean request(TBase<?, ?> data);
    boolean request(TBase<?, ?> data, int retry);
    boolean request(TBase<?, ?> data, FutureListener<ResponseMessage> listener);

    boolean addReconnectEventListener(ELAgentClientReconnectEventListener eventListener);
    boolean removeReconnectEventListener(ELAgentClientReconnectEventListener eventListener);

}
