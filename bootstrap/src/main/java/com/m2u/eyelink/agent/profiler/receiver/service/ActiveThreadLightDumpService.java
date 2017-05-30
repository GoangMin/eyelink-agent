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
import com.m2u.eyelink.context.thrift.TActiveThreadLightDump;
import com.m2u.eyelink.context.thrift.TCmdActiveThreadLightDump;
import com.m2u.eyelink.context.thrift.TCmdActiveThreadLightDumpRes;
import com.m2u.eyelink.context.thrift.TThreadLightDump;
import com.m2u.eyelink.util.JvmUtils;

public class ActiveThreadLightDumpService implements ProfilerRequestCommandService {

    private final ActiveTraceLocator activeTraceLocator;

    public ActiveThreadLightDumpService(ActiveTraceLocator activeTraceLocator) {
        this.activeTraceLocator = activeTraceLocator;
    }

    @Override
    public TBase<?, ?> requestCommandService(TBase tBase) {
        TCmdActiveThreadLightDump request = (TCmdActiveThreadLightDump) tBase;

        List<TActiveThreadLightDump> activeThreadDumpList = getActiveThreadDumpList(request);

        TCmdActiveThreadLightDumpRes response = new TCmdActiveThreadLightDumpRes();
        response.setType("JAVA");
        response.setSubType(JvmUtils.getType().name());
        response.setVersion(JvmUtils.getVersion().name());
        response.setThreadDumps(activeThreadDumpList);
        return response;
    }

    private List<TActiveThreadLightDump> getActiveThreadDumpList(TCmdActiveThreadLightDump request) {
        List<ActiveTraceInfo> activeTraceInfoList = activeTraceLocator.collect();

        int limit = request.getLimit();
        if (limit > 0) {
            Collections.sort(activeTraceInfoList, ActiveThreadDumpUtils.getActiveTraceInfoComparator());
        } else {
            limit = Integer.MAX_VALUE;
        }

        return getTActiveThreadDumpList(request, limit, activeTraceInfoList);
    }

    private List<TActiveThreadLightDump> getTActiveThreadDumpList(TCmdActiveThreadLightDump request, int limit, List<ActiveTraceInfo> activeTraceInfoList) {
        int targetThreadNameListSize = request.getThreadNameListSize();
        int localTraceIdListSize = request.getLocalTraceIdListSize();
        boolean filterEnable = (targetThreadNameListSize + localTraceIdListSize) > 0;

        List<TActiveThreadLightDump> activeThreadDumpList = new ArrayList<TActiveThreadLightDump>(Math.min(limit, activeTraceInfoList.size()));
        if (filterEnable) {
            for (ActiveTraceInfo activeTraceInfo : activeTraceInfoList) {
                if (!ActiveThreadDumpUtils.isTraceThread(activeTraceInfo, request.getThreadNameList(), request.getLocalTraceIdList())) {
                    continue;
                }

                TActiveThreadLightDump activeThreadDump = createActiveLightThreadDump(activeTraceInfo);
                if (activeThreadDump != null) {
                    if (limit > activeThreadDumpList.size()) {
                        activeThreadDumpList.add(activeThreadDump);
                    }
                }
            }
        } else {
            for (ActiveTraceInfo activeTraceInfo : activeTraceInfoList) {
                TActiveThreadLightDump activeThreadDump = createActiveLightThreadDump(activeTraceInfo);
                if (activeThreadDump != null) {
                    if (limit > activeThreadDumpList.size()) {
                        activeThreadDumpList.add(activeThreadDump);
                    }
                }
            }
        }

        return activeThreadDumpList;
    }

    private TActiveThreadLightDump createActiveLightThreadDump(ActiveTraceInfo activeTraceInfo) {
        Thread thread = activeTraceInfo.getThread();
        if (thread == null) {
            return null;
        }
        TThreadLightDump threadDump = createThreadDump(thread);
        return createActiveThreadDump(activeTraceInfo, threadDump);
    }


    private TThreadLightDump createThreadDump(Thread thread) {
        TThreadLightDump threadDump = new TThreadLightDump();
        threadDump.setThreadName(thread.getName());
        threadDump.setThreadId(thread.getId());
        threadDump.setThreadState(ThreadDumpUtils.toTThreadState(thread.getState()));
        return threadDump;
    }

    private TActiveThreadLightDump createActiveThreadDump(ActiveTraceInfo activeTraceInfo, TThreadLightDump threadDump) {
        TActiveThreadLightDump activeThreadDump = new TActiveThreadLightDump();
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
        return TCmdActiveThreadLightDump.class;
    }

}
