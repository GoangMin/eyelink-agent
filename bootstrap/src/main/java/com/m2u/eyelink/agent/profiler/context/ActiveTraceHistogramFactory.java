package com.m2u.eyelink.agent.profiler.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.m2u.eyelink.context.ActiveTraceInfo;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.trace.BaseHistogramSchema;
import com.m2u.eyelink.trace.HistogramSchema;
import com.m2u.eyelink.trace.HistogramSlot;
import com.m2u.eyelink.trace.SlotType;

public class ActiveTraceHistogramFactory {

    private final ActiveTraceLocator activeTraceLocator;
    private final int activeTraceSlotsCount;
    private final HistogramSchema histogramSchema = BaseHistogramSchema.NORMAL_SCHEMA;

    private static final List<SlotType> ACTIVE_TRACE_SLOTS_ORDER = new ArrayList<SlotType>();

    static {
        ACTIVE_TRACE_SLOTS_ORDER.add(SlotType.FAST);
        ACTIVE_TRACE_SLOTS_ORDER.add(SlotType.NORMAL);
        ACTIVE_TRACE_SLOTS_ORDER.add(SlotType.SLOW);
        ACTIVE_TRACE_SLOTS_ORDER.add(SlotType.VERY_SLOW);
    }

    public ActiveTraceHistogramFactory(ActiveTraceLocator activeTraceLocator) {
        if (activeTraceLocator == null) {
            throw new NullPointerException("activeTraceLocator must not be null");
        }
        this.activeTraceLocator = activeTraceLocator;
        this.activeTraceSlotsCount = ACTIVE_TRACE_SLOTS_ORDER.size();
    }

    public ActiveTraceHistogram createHistogram() {
        Map<SlotType, IntAdder> mappedSlot = new LinkedHashMap<SlotType, IntAdder>(activeTraceSlotsCount);
        for (SlotType slotType : ACTIVE_TRACE_SLOTS_ORDER) {
            mappedSlot.put(slotType, new IntAdder(0));
        }

        long currentTime = System.currentTimeMillis();

        List<ActiveTraceInfo> collectedActiveTraceInfo = activeTraceLocator.collect();
        for (ActiveTraceInfo activeTraceInfo : collectedActiveTraceInfo) {
            HistogramSlot slot = histogramSchema.findHistogramSlot((int) (currentTime - activeTraceInfo.getStartTime()), false);
            mappedSlot.get(slot.getSlotType()).incrementAndGet();
        }

        List<Integer> activeTraceCount = new ArrayList<Integer>(activeTraceSlotsCount);
        for (IntAdder statusCount : mappedSlot.values()) {
            activeTraceCount.add(statusCount.get());
        }
        return new ActiveTraceHistogram(this.histogramSchema, activeTraceCount);
    }

    private static class IntAdder {
        private int value = 0;

        public IntAdder(int defaultValue) {
            this.value = defaultValue;
        }

        public int incrementAndGet() {
            return ++value;
        }

        public int get() {
            return this.value;
        }
    }

    public static class ActiveTraceHistogram {

        private final HistogramSchema histogramSchema;
        private final List<Integer> activeTraceCounts;

        private ActiveTraceHistogram(HistogramSchema histogramSchema, List<Integer> activeTraceCounts) {
            this.histogramSchema = histogramSchema;
            if (activeTraceCounts == null) {
                this.activeTraceCounts = Collections.emptyList();
            } else {
                this.activeTraceCounts = activeTraceCounts;
            }
        }

        public HistogramSchema getHistogramSchema() {
            return histogramSchema;
        }

        public List<Integer> getActiveTraceCounts() {
            return activeTraceCounts;
        }
    }
}
