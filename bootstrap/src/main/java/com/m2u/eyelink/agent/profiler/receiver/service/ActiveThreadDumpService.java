package com.m2u.eyelink.agent.profiler.receiver.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.thrift.TBase;

import com.m2u.eyelink.agent.profiler.receiver.ProfilerRequestCommandService;
import com.m2u.eyelink.agent.profiler.util.ActiveThreadDumpUtils;
import com.m2u.eyelink.agent.profiler.util.ThreadDumpUtils;
import com.m2u.eyelink.context.ActiveTraceInfo;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.context.thrift.TActiveThreadDump;
import com.m2u.eyelink.context.thrift.TCmdActiveThreadDump;
import com.m2u.eyelink.context.thrift.TCmdActiveThreadDumpRes;
import com.m2u.eyelink.context.thrift.TThreadDump;
import com.m2u.eyelink.util.JvmUtils;

public class ActiveThreadDumpService implements ProfilerRequestCommandService {

    private final ActiveTraceLocator activeTraceLocator;

    public ActiveThreadDumpService(ActiveTraceLocator activeTraceLocator) {
        this.activeTraceLocator = activeTraceLocator;
    }

    @Override
    public TBase<?, ?> requestCommandService(TBase tBase) {
        TCmdActiveThreadDump request = (TCmdActiveThreadDump) tBase;

        List<TActiveThreadDump> activeThreadDumpList = getActiveThreadDumpList(request);

        TCmdActiveThreadDumpRes response = new TCmdActiveThreadDumpRes();
        response.setType("JAVA");
        response.setSubType(JvmUtils.getType().name());
        response.setVersion(JvmUtils.getVersion().name());
        response.setThreadDumps(activeThreadDumpList);
        return response;
    }

    private List<TActiveThreadDump> getActiveThreadDumpList(TCmdActiveThreadDump request) {
        List<ActiveTraceInfo> activeTraceInfoList = activeTraceLocator.collect();

        int limit = request.getLimit();
        if (limit > 0) {
            Collections.sort(activeTraceInfoList, ActiveThreadDumpUtils.getActiveTraceInfoComparator());
        } else {
            limit = Integer.MAX_VALUE;
        }

        return getActiveThreadDumpList(request, limit, activeTraceInfoList);
    }

    private List<TActiveThreadDump> getActiveThreadDumpList(TCmdActiveThreadDump request, int limit, List<ActiveTraceInfo> activeTraceInfoList) {
        int targetThreadNameListSize = request.getThreadNameListSize();
        int localTraceIdListSize = request.getLocalTraceIdListSize();
        boolean filterEnable = (targetThreadNameListSize + localTraceIdListSize) > 0;

        List<TActiveThreadDump> activeThreadDumpList = new ArrayList<TActiveThreadDump>(Math.min(limit, activeTraceInfoList.size()));
        if (filterEnable) {
            for (ActiveTraceInfo activeTraceInfo : activeTraceInfoList) {
                if (!ActiveThreadDumpUtils.isTraceThread(activeTraceInfo, request.getThreadNameList(), request.getLocalTraceIdList())) {
                    continue;
                }

                TActiveThreadDump activeThreadDump = createActiveThreadDump(activeTraceInfo);
                if (activeThreadDump != null) {
                    if (limit > activeThreadDumpList.size()) {
                        activeThreadDumpList.add(activeThreadDump);
                    }
                }
            }
        } else {
            for (ActiveTraceInfo activeTraceInfo : activeTraceInfoList) {
                TActiveThreadDump activeThreadDump = createActiveThreadDump(activeTraceInfo);
                if (activeThreadDump != null) {
                    if (limit > activeThreadDumpList.size()) {
                        activeThreadDumpList.add(activeThreadDump);
                    }
                }
            }
        }

        return activeThreadDumpList;
    }

    private TActiveThreadDump createActiveThreadDump(ActiveTraceInfo activeTraceInfo) {
        Thread thread = activeTraceInfo.getThread();
        TThreadDump threadDump = createThreadDump(thread, true);
        if (threadDump != null) {
            return createTActiveThreadDump(activeTraceInfo, threadDump);
        }
        return null;
    }

    private TThreadDump createThreadDump(Thread thread, boolean isIncludeStackTrace) {
        if (thread == null) {
            return null;
        }

        if (isIncludeStackTrace) {
            return ThreadDumpUtils.createTThreadDump(thread);
        } else {
            return ThreadDumpUtils.createTThreadDump(thread, 0);
        }
    }

    private TActiveThreadDump createTActiveThreadDump(ActiveTraceInfo activeTraceInfo, TThreadDump threadDump) {
        TActiveThreadDump activeThreadDump = new TActiveThreadDump();
        activeThreadDump.setStartTime(activeTraceInfo.getStartTime());
        activeThreadDump.setLocalTraceId(activeTraceInfo.getLocalTraceId());
        activeThreadDump.setThreadDump(threadDump);

        if (activeTraceInfo.isSampled()) {
            activeThreadDump.setSampled(true);
            activeThreadDump.setTransactionId(activeTraceInfo.getTransactionId());
            activeThreadDump.setEntryPoint(activeTraceInfo.getEntryPoint());
        }
        return activeThreadDump;
    }

    @Override
    public Class<? extends TBase> getCommandClazz() {
        return TCmdActiveThreadDump.class;
    }

}
