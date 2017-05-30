package com.m2u.eyelink.context;

public class AsyncStorage implements Storage {

    private Storage storage;
    
    public AsyncStorage(final Storage storage) {
        this.storage = storage;
    }
    
    @Override
    public void store(SpanEvent spanEvent) {
        storage.store(spanEvent);
    }

    @Override
    public void store(Span span) {
        storage.flush();
    }

    @Override
    public void flush() {
        storage.flush();
    }

    @Override
    public void close() {
        storage.close();
    }
}
