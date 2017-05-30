package com.m2u.eyelink.agent;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.m2u.eyelink.common.ELConstants;
import com.m2u.eyelink.logging.ELLogger;

public class AgentDirBaseClassPathResolver implements ClassPathResolver {
	private final ELLogger logger = ELLogger.getLogger(this.getClass()
			.getName());

	static final String VERSION_PATTERN = "(-[0-9]+\\.[0-9]+\\.[0-9]+((\\-SNAPSHOT)|(-RC[0-9]+))?)?";
	static final Pattern DEFAULT_AGENT_PATTERN = compile("eyelink-agent"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_AGENT_COMMONS_PATTERN = compile("eyelink-agent-commons"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_AGENT_CORE_PATTERN = compile("eyelink-agent-core"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_AGENT_CORE_OPTIONAL_PATTERN = compile("eyelink-agent-optional"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_ANNOTATIONS = compile("eyelink-agent-annotations"
			+ VERSION_PATTERN + "\\.jar");

	private final Pattern agentPattern;
	private final Pattern agentCommonsPattern;
	private final Pattern agentCorePattern;
	private final Pattern agentCoreOptionalPattern;
	private final Pattern annotationsPattern;

	private String classPath;

	private String agentJarName;
	private String agentJarFullPath;
	private String agentDirPath;

	private List<String> fileExtensionList;
	private String ElAgentCommonsJar;
	private String ElAgentCoreJar;
	private String ElAgentOptionalJar;
	private String annotationsJar;

	private ELAgentJarFile agentJarFile;

	private static Pattern compile(String regex) {
		return Pattern.compile(regex);
	}

	public AgentDirBaseClassPathResolver() {
		this(getClassPathFromSystemProperty());
	}

	public AgentDirBaseClassPathResolver(String classPath) {
		this.classPath = classPath;
		this.agentPattern = DEFAULT_AGENT_PATTERN;
		this.agentCommonsPattern = DEFAULT_AGENT_COMMONS_PATTERN;
		this.agentCorePattern = DEFAULT_AGENT_CORE_PATTERN;
		this.agentCoreOptionalPattern = DEFAULT_AGENT_CORE_OPTIONAL_PATTERN;
		this.annotationsPattern = DEFAULT_ANNOTATIONS;
		this.fileExtensionList = getDefaultFileExtensionList();
	}

	public List<String> getDefaultFileExtensionList() {
		List<String> extensionList = new ArrayList<String>();
		extensionList.add("jar");
		extensionList.add("xml");
		extensionList.add("properties");
		return extensionList;
	}

	public static String getClassPathFromSystemProperty() {
		return System.getProperty("java.class.path");
	}

	@Override
	public boolean verify() {
		final ELAgentJarFile agentJarFile = new ELAgentJarFile();

		// find ELAgent.jar file
		final boolean agentJarFileFound = this.findAgentJar();
		if (!agentJarFileFound) {
			logger.warn("eyelink-agent-x.x.x(-SNAPSHOT).jar not found.");
			return false;
		}

		this.agentJarFile = agentJarFile;
		return true;
	}

	public boolean findAgentJar() {
		logger.info("classpath:" + classPath);
		logger.info(agentPattern.toString());
		// classpath 내에서 eyelink-agent-xxx.jar 파일을 찾음.
		Matcher matcher = agentPattern.matcher(classPath);
		if (!matcher.find()) {
			return false;
		}
		
		this.agentJarName = parseAgentJar(matcher);
		this.agentJarFullPath = parseAgentJarPath(classPath, agentJarName);
		logger.info("Agent Jar Name:" + agentJarName);
		logger.info("Agent Jar Full Path:" + agentJarFullPath);
		if (agentJarFullPath == null) {
			return false;
		}
		this.agentDirPath = parseAgentDirPath(agentJarFullPath);
		logger.info("Agent Dir Path:" + agentDirPath);
		if (agentDirPath == null) {
			return false;
		}

		logger.info("Agent original-path:" + agentDirPath);
		// defense alias change
		this.agentDirPath = toCanonicalPath(agentDirPath);
		logger.info("Agent canonical-path:" + agentDirPath);

		this.ElAgentCommonsJar = findFromBootDir("eyelink-agent-commons.jar",
				agentCommonsPattern);
		this.ElAgentCoreJar = findFromBootDir("eyelink-agent-core.jar",
				agentCorePattern);
		this.ElAgentOptionalJar = findFromBootDir("eyelink-agent-core-optional.jar",
				agentCoreOptionalPattern);
		this.annotationsJar = findFromBootDir("eyelink-agent-annotations.jar",
				annotationsPattern);
		return true;
	}

	private String parseAgentJar(Matcher matcher) {
		int start = matcher.start();
		int end = matcher.end();
		return this.classPath.substring(start, end);
	}

	private String parseAgentJarPath(String classPath, String agentJar) {
		String[] classPathList = classPath.split(File.pathSeparator);
		for (String findPath : classPathList) {
			boolean find = findPath.contains(agentJar);
			if (find) {
				return findPath;
			}
		}
		return null;
	}

	private String parseAgentDirPath(String agentJarFullPath) {
		int index1 = agentJarFullPath.lastIndexOf("/");
		int index2 = agentJarFullPath.lastIndexOf("\\");
		int max = Math.max(index1, index2);
		if (max == -1) {
			return null;
		}
		return agentJarFullPath.substring(0, max);
	}

	private String toCanonicalPath(String path) {
		final File file = new File(path);
		return toCanonicalPath(file);
	}

	private String toCanonicalPath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			logger.warn(file.getPath() + " getCanonicalPath() error. Error:"
					+ e.getMessage(), e);
			return file.getAbsolutePath();
		}
	}

	private String findFromBootDir(final String name, final Pattern pattern) {
		String bootDirPath = agentDirPath + File.separator + "boot";
		File[] files = listFiles(name, pattern, bootDirPath);
		if (files == null || files.length == 0) {
			logger.info(name + " not found.");
			return null;
		} else if (files.length == 1) {
			File file = files[0];
			return toCanonicalPath(file);
		} else {
			logger.info("too many " + name + " found. "
					+ Arrays.toString(files));
			return null;
		}
	}

	private File[] listFiles(final String name, final Pattern pattern,
			String bootDirPath) {
		File bootDir = new File(bootDirPath);
		return bootDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				Matcher matcher = pattern.matcher(fileName);
				if (matcher.matches()) {

					logger.info("found " + name + ". " + dir.getAbsolutePath()
							+ File.separator + fileName);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public ELAgentJarFile getELAgentJarFile() {
		return agentJarFile;
	}

	@Override
	public String getELAgentCommonsJar() {
		return ElAgentCommonsJar;
	}

	@Override
	public String getELAgentCoreJar() {
		return ElAgentCoreJar;
	}

	@Override
	public String getELAgentOptionalJar() {
		return ElAgentOptionalJar;
	}

	@Override
	public String getAgentJarName() {
		return this.agentJarName;
	}

	@Override
	public String getAgentJarFullPath() {
		return this.agentJarFullPath;
	}

	@Override
	public String getAgentLibPath() {
		return this.agentDirPath + File.separator + "lib";
	}

	@Override
	public String getAgentLogFilePath() {
		return this.agentDirPath + File.separator + "log";
	}

	@Override
	public String getAgentPluginPath() {
		return this.agentDirPath + File.separator + "plugin";
	}

	@Override
	public List<URL> resolveLib() {
	       String agentLibPath = getAgentLibPath();
	        File libDir = new File(agentLibPath);
	        if (!libDir.exists()) {
	            logger.warn(agentLibPath + " not found");
	            return Collections.emptyList();
	        }
	        if (!libDir.isDirectory()) {
	            logger.warn(agentLibPath + " not Directory");
	            return Collections.emptyList();
	        }
	        final List<URL> jarURLList =  new ArrayList<URL>();

	        final File[] findJarList = findJar(libDir);
	        if (findJarList != null) {
	            for (File file : findJarList) {
	                URL url = toURI(file);
	                if (url != null) {
	                    jarURLList.add(url);
	                }
	            }
	        }

	        URL agentDirUri = toURI(new File(agentLibPath));
	        if (agentDirUri != null) {
	            jarURLList.add(agentDirUri);
	        }
	        
	        // FIXME commonjar, corejar가 없으므로 SKIP 처리함.

	        // hot fix. boot jars not found from classPool ??
//	        jarURLList.add(toURI(new File(getELAgentCommonsJar())));
//	        jarURLList.add(toURI(new File(getELAgentCoreJar())));
	        String bootstrapCoreOptionalJar = getELAgentOptionalJar();
	        // bootstrap-core-optional jar is not required and is okay to be null
	        if (bootstrapCoreOptionalJar != null) {
	            jarURLList.add(toURI(new File(bootstrapCoreOptionalJar)));
	        }

	        return jarURLList;
	}

	@Override
	public URL[] resolvePlugins() {
		final File file = new File(getAgentPluginPath());

		if (!file.exists()) {
			logger.warn(file + " not found");
			return new URL[0];
		}

		if (!file.isDirectory()) {
			logger.warn(file + " is not a directory");
			return new URL[0];
		}

		final File[] jars = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		if (jars == null || jars.length == 0) {
			return new URL[0];
		}

		final URL[] urls = new URL[jars.length];

		for (int i = 0; i < jars.length; i++) {
			try {
				urls[i] = jars[i].toURI().toURL();
			} catch (MalformedURLException e) {
				// TODO have to change to PinpointException AFTER moving the
				// exception to pinpoint-common
				throw new RuntimeException("Fail to load plugin jars", e);
			}
		}

		for (File pluginJar : jars) {
			logger.info("Found plugins: " + pluginJar.getPath());
		}

		return urls;
	}

    private URL toURI(File file) {
        URI uri = file.toURI();
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            logger.warn(file.getName() + ".toURL() failed.", e);
            return null;
        }
    }
	@Override
	public String getAgentDirPath() {
		return agentDirPath;
	}

	@Override
	public String getAgentConfigPath() {
		// TODO 임시로 config path 지정함.
//		return agentDirPath + File.separator + ELConstants.ProductName + ".config";
		return agentDirPath + File.separator + "classes" + File.separator + ELConstants.ProductName + ".config";
	}
	
	   private File[] findJar(File libDir) {
	        return libDir.listFiles(new FileFilter() {
	            @Override
	            public boolean accept(File pathname) {
	                String path = pathname.getName();
	                for (String extension : fileExtensionList) {
	                    if (path.lastIndexOf("." + extension) != -1) {
	                        return true;
	                    }
	                }
	                return false;
	            }
	        });
	    }
}
