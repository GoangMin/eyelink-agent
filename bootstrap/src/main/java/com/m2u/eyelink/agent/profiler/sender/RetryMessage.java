package com.m2u.eyelink.agent.profiler.sender;

public class RetryMessage {

    private int retryCount = 0;
    private final int maxRetryCount;

    private final byte[] bytes;
    private final String messageDescription;

    public RetryMessage(int maxRetryCount, byte[] bytes) {
        this(0, maxRetryCount, bytes, "");
    }

    public RetryMessage(int retryCount, int maxRetryCount, byte[] bytes) {
        this(retryCount, maxRetryCount, bytes, "");
    }

    public RetryMessage(int maxRetryCount, byte[] bytes, String messageDescription) {
        this(0, maxRetryCount, bytes, messageDescription);
    }

    public RetryMessage(int retryCount, int maxRetryCount, byte[] bytes, String messageDescription) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount:" + retryCount + " must be positive number");
        }
        if (maxRetryCount < 0) {
            throw new IllegalArgumentException("maxRetryCount:" + maxRetryCount + " must be positive number");
        }
        if (retryCount > maxRetryCount) {
            throw new IllegalArgumentException("maxRetryCount(" + maxRetryCount + ") must be greater than retryCount(" + retryCount + ")");
        }

        this.retryCount = retryCount;
        this.maxRetryCount = maxRetryCount;
        this.bytes = bytes;
        this.messageDescription = messageDescription;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public boolean isRetryAvailable() {
        return retryCount < maxRetryCount;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int fail() {
        return ++retryCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetryMessage{");
        sb.append("retryCount=").append(retryCount);
        sb.append(", maxRetryCount=").append(maxRetryCount);
        sb.append(", bytes=").append(getLength(bytes));
        sb.append(", messageDescription='").append(messageDescription).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private int getLength(byte[] bytes) {
        if (bytes == null) {
            return -1;
        }
        return bytes.length;
    }

}
