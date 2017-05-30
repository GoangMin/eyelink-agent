package com.m2u.eyelink.agent.profiler.plugin;

import com.m2u.eyelink.config.ProfilerConfig;

public interface ProfilerPluginSetupContext {
    /**
     * Get the {@link ProfilerConfig}
     * 
     * @return {@link ProfilerConfig}
     */
    ProfilerConfig getConfig();

    /**
     * Add a {@link ApplicationTypeDetector} to Pinpoint agent.
     * 
     * @param detectors
     */
    void addApplicationTypeDetector(ApplicationTypeDetector... detectors);

}