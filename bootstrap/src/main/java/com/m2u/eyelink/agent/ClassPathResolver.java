package com.m2u.eyelink.agent;

import java.net.URL;
import java.util.List;

public interface ClassPathResolver {
    boolean verify();

    ELAgentJarFile getELAgentJarFile();

    String getELAgentCommonsJar();

    String getELAgentCoreJar();

    String getELAgentOptionalJar();

    String getAgentJarName();

    String getAgentJarFullPath();

    String getAgentLibPath();

    String getAgentLogFilePath();

    String getAgentPluginPath();

    List<URL> resolveLib();

    URL[] resolvePlugins();

    String getAgentDirPath();

    String getAgentConfigPath();
}
