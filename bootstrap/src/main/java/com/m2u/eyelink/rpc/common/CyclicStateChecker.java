package com.m2u.eyelink.rpc.common;

public class CyclicStateChecker {

    private final byte conditionValue;

    private final int capacity;

    private byte data = 0;

    private int index = 0;

    // no guarantee of synchronization
    public CyclicStateChecker(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + capacity + ". Available 1 ~ 8.");
        }

        if (capacity > 8) {
            throw new IllegalArgumentException("Illegal Capacity: " + capacity + ". Available 1 ~ 8.");
        }

        byte conditionValue = 0;
        for (int i = 0; i < capacity; i++) {
            conditionValue |= 1 << i;
        }

        this.capacity = capacity;
        this.conditionValue = conditionValue;
    }

    public boolean markAndCheckCondition() {
        index++;
        index %= capacity;

        // 0000 1000 or operation
        data |= 1 << index;
        if (data == conditionValue) {
            return true;
        }
        return false;
    }

    public void unmark() {
        index++;
        index %= capacity;

        // 1111 0111 and operation
        data &= conditionValue - (1 << index);
    }

    public boolean checkCondition() {
        if (data == conditionValue) {
            return true;
        }

        return false;
    }

}