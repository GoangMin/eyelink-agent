package com.m2u.eyelink.agent.profiler.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.instrument.GuardInstrumentContext;
import com.m2u.eyelink.agent.instrument.InstrumentContext;
import com.m2u.eyelink.agent.instrument.transformer.TransformTemplate;
import com.m2u.eyelink.agent.instrument.transformer.TransformTemplateAware;
import com.m2u.eyelink.agent.plugin.ProfilerPlugin;
import com.m2u.eyelink.agent.profiler.DefaultAgent;
import com.m2u.eyelink.agent.profiler.instrument.ClassInjector;
import com.m2u.eyelink.agent.profiler.instrument.JarProfilerPluginClassInjector;
import com.m2u.eyelink.common.plugin.PluginLoader;
import com.m2u.eyelink.util.StringUtils;

public class ProfilerPluginLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DefaultAgent agent;

    private final ClassNameFilter profilerPackageFilter = new ELAgentProfilerPackageSkipFilter();

    public ProfilerPluginLoader(DefaultAgent agent) {
        if (agent == null) {
            throw new NullPointerException("agent must not be null");
        }
        this.agent = agent;
    }
    
    public List<DefaultProfilerPluginContext> load(URL[] pluginJars) {
        List<DefaultProfilerPluginContext> pluginContexts = new ArrayList<DefaultProfilerPluginContext>(pluginJars.length);
        List<String> disabled = agent.getProfilerConfig().getDisabledPlugins();
        
        for (URL jar : pluginJars) {

            final JarFile pluginJarFile = createJarFile(jar);
            final List<String> pluginPackageList = getPluginPackage(pluginJarFile);

            final ClassNameFilter pluginFilterChain = createPluginFilterChain(pluginPackageList);

            final List<ProfilerPlugin> plugins = PluginLoader.load(ProfilerPlugin.class, new URL[] { jar });

            for (ProfilerPlugin plugin : plugins) {
                if (disabled.contains(plugin.getClass().getName())) {
                    logger.info("Skip disabled plugin: {}", plugin.getClass().getName());
                    continue;
                }
                if (logger.isInfoEnabled()) {
                    logger.info("{} Plugin {}:{}", plugin.getClass(), PluginConfig.PINPOINT_PLUGIN_PACKAGE, pluginPackageList);
                }
                
                logger.info("Loading plugin:{} pluginPackage:{}", plugin.getClass().getName(), plugin);

                PluginConfig pluginConfig = new PluginConfig(jar, plugin, agent.getInstrumentation(), agent.getClassPool(), agent.getBootstrapJarPaths(), pluginFilterChain);
                final DefaultProfilerPluginContext context = setupPlugin(pluginConfig);
                pluginContexts.add(context);
            }
        }
        

        return pluginContexts;
    }

    private ClassNameFilter createPluginFilterChain(List<String> packageList) {

        final ClassNameFilter pluginPackageFilter = new PluginPackageFilter(packageList);

        final List<ClassNameFilter> chain = Arrays.asList(profilerPackageFilter, pluginPackageFilter);

        final ClassNameFilter filterChain = new ClassNameFilterChain(chain);

        return filterChain;
    }

    private JarFile createJarFile(URL pluginJar) {
        try {
            final URI uri = pluginJar.toURI();
            return new JarFile(new File(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException("URISyntax error. " + e.getCause(), e);
        } catch (IOException e) {
            throw new RuntimeException("IO error. " + e.getCause(), e);
        }
    }
    private Manifest getManifest(JarFile pluginJarFile) {
        try {
            return pluginJarFile.getManifest();
        } catch (IOException ex) {
            logger.info("{} IoError :{}", pluginJarFile.getName(), ex.getMessage(), ex);
            return null;
        }
    }

    public List<String> getPluginPackage(JarFile pluginJarFile) {

        final Manifest manifest =  getManifest(pluginJarFile);
        if (manifest == null) {
            return PluginConfig.DEFAULT_PINPOINT_PLUGIN_PACKAGE_NAME;
        }

        final Attributes attributes = manifest.getMainAttributes();
        final String pluginPackage = attributes.getValue(PluginConfig.PINPOINT_PLUGIN_PACKAGE);
        if (pluginPackage == null) {
            return PluginConfig.DEFAULT_PINPOINT_PLUGIN_PACKAGE_NAME;
        }
        return StringUtils.splitAndTrim(pluginPackage, ",");
    }


    private GuardInstrumentContext preparePlugin(ProfilerPlugin plugin, InstrumentContext context) {

        final GuardInstrumentContext guardInstrumentContext = new GuardInstrumentContext(context);
        if (plugin instanceof TransformTemplateAware) {
            if (logger.isDebugEnabled()) {
                logger.debug("{}.setTransformTemplate", plugin.getClass().getName());
            }
            final TransformTemplate transformTemplate = new TransformTemplate(guardInstrumentContext);
            ((TransformTemplateAware) plugin).setTransformTemplate(transformTemplate);
        }
        return guardInstrumentContext;
    }

    private DefaultProfilerPluginContext setupPlugin(PluginConfig pluginConfig) {
        final ClassInjector classInjector = new JarProfilerPluginClassInjector(pluginConfig);
        final DefaultProfilerPluginContext context = new DefaultProfilerPluginContext(agent, classInjector);

        final GuardProfilerPluginContext guardPluginContext = new GuardProfilerPluginContext(context);
        final GuardInstrumentContext guardInstrumentContext = preparePlugin(pluginConfig.getPlugin(), context);
        try {
            // WARN external plugin api
            final ProfilerPlugin plugin = pluginConfig.getPlugin();
            if (logger.isInfoEnabled()) {
                logger.info("{} Plugin setup", plugin.getClass().getName());
            }
            plugin.setup(guardPluginContext);
        } finally {
            guardPluginContext.close();
            guardInstrumentContext.close();
        }
        return context;
    }

}
