package com.m2u.eyelink.agent.profiler.plugin;

import java.util.ArrayList;
import java.util.List;

public class PluginPackageFilter implements ClassNameFilter {

    private final List<String> packageList;

    public PluginPackageFilter(List<String> packageList) {
        if (packageList == null) {
            throw new NullPointerException("packageList must not be null");
        }
        this.packageList = new ArrayList<String>(packageList);
    }

    @Override
    public boolean accept(String className) {
        for (String packageName : packageList) {
            if (className.startsWith(packageName)) {
                return ACCEPT;
            }
        }
        return REJECT;
    }

    @Override
    public String toString() {
        return "PluginPackageFilter{" +
                "packageList=" + packageList +
                '}';
    }
}

