package com.m2u.eyelink.agent.profiler.instrument;

import com.m2u.eyelink.agent.profiler.plugin.PluginConfig;


public interface PluginClassInjector extends ClassInjector {

    PluginConfig getPluginConfig();
}
