package com.m2u.eyelink.agent.profiler.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

import com.m2u.eyelink.agent.instrument.InstrumentClassPool;
import com.m2u.eyelink.agent.plugin.ProfilerPlugin;
import com.m2u.eyelink.logging.ELLogger;

public class PluginConfig {

	private final ELLogger logger = ELLogger.getLogger(this.getClass()
			.getName());
    public static final String PINPOINT_PLUGIN_PACKAGE = "Pinpoint-Plugin-Package";
    public static final List<String> DEFAULT_PINPOINT_PLUGIN_PACKAGE_NAME = Collections.singletonList("com.navercorp.pinpoint.plugin");

    private final URL pluginJar;
    private final JarFile pluginJarFile;
    private String pluginJarURLExternalForm;

    private final ProfilerPlugin plugin;

    private final Instrumentation instrumentation;
    private final InstrumentClassPool classPool;
    private final List<String> bootstrapJarPaths;

    private final ClassNameFilter pluginPackageFilter;

    public PluginConfig(URL pluginJar, ProfilerPlugin plugin, Instrumentation instrumentation, InstrumentClassPool classPool, List<String> bootstrapJarPaths, ClassNameFilter pluginPackageFilter) {
        if (pluginJar == null) {
            throw new NullPointerException("pluginJar must not be null");
        }
        if (plugin == null) {
            throw new NullPointerException("plugin must not be null");
        }
        if (bootstrapJarPaths == null) {
            throw new NullPointerException("bootstrapJarPaths must not be null");
        }
        this.pluginJar = pluginJar;
        this.pluginJarFile = createJarFile(pluginJar);
        this.plugin = plugin;

        this.instrumentation = instrumentation;
        this.classPool = classPool;
        this.bootstrapJarPaths = bootstrapJarPaths;

        this.pluginPackageFilter = pluginPackageFilter;
    }




    public ProfilerPlugin getPlugin() {
        return plugin;
    }

    public URL getPluginJar() {
        return pluginJar;
    }

    public JarFile getPluginJarFile() {
        return pluginJarFile;
    }

    public String getPluginJarURLExternalForm() {
        if (this.pluginJarURLExternalForm == null) {
            this.pluginJarURLExternalForm = pluginJar.toExternalForm();
        }
        return this.pluginJarURLExternalForm;
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

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public InstrumentClassPool getClassPool() {
        return classPool;
    }

    public List<String> getBootstrapJarPaths() {
        return bootstrapJarPaths;
    }

    public ClassNameFilter getPluginPackageFilter() {
        return pluginPackageFilter;
    }

    @Override
    public String toString() {
        return "PluginConfig{" +
                "pluginJar=" + pluginJar +
                ", pluginJarFile=" + pluginJarFile +
                ", pluginJarURLExternalForm='" + pluginJarURLExternalForm + '\'' +
                ", plugin=" + plugin +
                ", instrumentation=" + instrumentation +
                ", classPool=" + classPool +
                ", bootstrapJarPaths=" + bootstrapJarPaths +
                ", pluginPackageFilter=" + pluginPackageFilter +
                '}';
    }
}