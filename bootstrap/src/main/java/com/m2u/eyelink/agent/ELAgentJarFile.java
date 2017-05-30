package com.m2u.eyelink.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class ELAgentJarFile {
    private final List<JarFile> jarFileEntry = new ArrayList<JarFile>();

    public ELAgentJarFile() {
    }

    public void append(JarFile jarFile) {
        if (jarFile == null) {
            throw new NullPointerException("jarFile must not be null");
        }

        this.jarFileEntry.add(jarFile);
    }

    public List<JarFile> getJarFileList() {
        return jarFileEntry;
    }

    public List<String> getJarNameList() {
        List<String> JarLIst = new ArrayList<String>(jarFileEntry.size());
        for (JarFile jarFile : jarFileEntry) {
            JarLIst.add(jarFile.getName());
        }
        return JarLIst;
    }
}
