package com.m2u.eyelink.agent.profiler.plugin;

import com.m2u.eyelink.config.ProfilerConfig;

public class GuardProfilerPluginContext implements ProfilerPluginSetupContext {

    private final ProfilerPluginSetupContext delegate;
    private boolean close = false;

    public GuardProfilerPluginContext(ProfilerPluginSetupContext delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate must not be null");
        }
        this.delegate = delegate;
    }



    @Override
    public ProfilerConfig getConfig() {
//        checkOpen();
        return this.delegate.getConfig();
    }

    @Override
    public void addApplicationTypeDetector(ApplicationTypeDetector... detectors) {
        checkOpen();
        this.delegate.addApplicationTypeDetector(detectors);
    }

    private void checkOpen() {
        if (close) {
            throw new IllegalStateException("ProfilerPluginSetupContext already initialized");
        }
    }

    public void close() {
        this.close = true;
    }
}
