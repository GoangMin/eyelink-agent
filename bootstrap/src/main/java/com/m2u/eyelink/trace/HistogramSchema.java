package com.m2u.eyelink.trace;

public interface HistogramSchema {
    int getTypeCode();

    /**
     * find the most appropriate slot based on elapsedTime
     *
     * @param elapsedTime
     * @return
     */
    HistogramSlot findHistogramSlot(int elapsedTime, boolean error);

    HistogramSlot getFastSlot();

    HistogramSlot getNormalSlot();

    HistogramSlot getSlowSlot();

    HistogramSlot getVerySlowSlot();

    HistogramSlot getErrorSlot();

    HistogramSlot getFastErrorSlot();

    HistogramSlot getNormalErrorSlot();

    HistogramSlot getSlowErrorSlot();

    HistogramSlot getVerySlowErrorSlot();
}
