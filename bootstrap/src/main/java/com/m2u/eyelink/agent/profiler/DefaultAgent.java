package com.m2u.eyelink.agent.profiler;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.Agent;
import com.m2u.eyelink.agent.AgentOption;
import com.m2u.eyelink.agent.ProductInfo;
import com.m2u.eyelink.agent.instrument.InstrumentClassPool;
import com.m2u.eyelink.agent.interceptor.InterceptorInvokerHelper;
import com.m2u.eyelink.agent.profiler.context.TransactionCounter;
import com.m2u.eyelink.agent.profiler.context.storage.BufferedStorageFactory;
import com.m2u.eyelink.agent.profiler.context.storage.SpanStorageFactory;
import com.m2u.eyelink.agent.profiler.instrument.ASMBytecodeDumpService;
import com.m2u.eyelink.agent.profiler.instrument.ASMClassPool;
import com.m2u.eyelink.agent.profiler.instrument.BytecodeDumpTransformer;
import com.m2u.eyelink.agent.profiler.instrument.JavassistClassPool;
import com.m2u.eyelink.agent.profiler.interceptor.registry.DefaultInterceptorRegistryBinder;
import com.m2u.eyelink.agent.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.m2u.eyelink.agent.profiler.monitor.AgentStatCollectorFactory;
import com.m2u.eyelink.agent.profiler.monitor.AgentStatMonitor;
import com.m2u.eyelink.agent.profiler.plugin.DefaultProfilerPluginContext;
import com.m2u.eyelink.agent.profiler.plugin.ProfilerPluginLoader;
import com.m2u.eyelink.agent.profiler.receiver.CommandDispatcher;
import com.m2u.eyelink.agent.profiler.receiver.ProfilerCommandLocatorBuilder;
import com.m2u.eyelink.agent.profiler.receiver.service.ActiveThreadService;
import com.m2u.eyelink.agent.profiler.receiver.service.EchoService;
import com.m2u.eyelink.agent.profiler.sampler.SamplerFactory;
import com.m2u.eyelink.agent.profiler.sender.TcpDataSender;
import com.m2u.eyelink.agent.profiler.sender.UdpDataSenderFactory;
import com.m2u.eyelink.agent.profiler.util.ApplicationServerTypeResolver;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.context.ActiveTraceLocator;
import com.m2u.eyelink.context.AgentInformation;
import com.m2u.eyelink.context.DefaultServerMetaDataHolder;
import com.m2u.eyelink.context.DefaultTraceContext;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.context.RuntimeMXBeanUtils;
import com.m2u.eyelink.context.Sampler;
import com.m2u.eyelink.context.ServerMetaDataHolder;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.context.TraceContext;
import com.m2u.eyelink.logging.PLogger;
import com.m2u.eyelink.logging.PLoggerBinder;
import com.m2u.eyelink.logging.PLoggerFactory;
import com.m2u.eyelink.logging.Slf4jLoggerBinder;
import com.m2u.eyelink.plugin.tomcat.DefaultProfilerConfig;
import com.m2u.eyelink.rpc.ClassPreLoader;
import com.m2u.eyelink.rpc.client.ELAgentClient;
import com.m2u.eyelink.rpc.client.ELAgentClientFactory;
import com.m2u.eyelink.rpc.util.ClientFactoryUtils;
import com.m2u.eyelink.sender.DataSender;
import com.m2u.eyelink.sender.EnhancedDataSender;
import com.m2u.eyelink.trace.ServiceType;

public class DefaultAgent implements Agent {

	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PLoggerBinder binder;

    private final ClassFileTransformerDispatcher classFileTransformer;
    
    private final ProfilerConfig profilerConfig;

    private final AgentInfoSender agentInfoSender;
    private final AgentStatMonitor agentStatMonitor;

    private final TraceContext traceContext;

    private ELAgentClientFactory clientFactory;
    private ELAgentClient client;
    private final EnhancedDataSender tcpDataSender;

    private final DataSender statDataSender;
    private final DataSender spanDataSender;

    private final AgentInformation agentInformation;
    private final ServerMetaDataHolder serverMetaDataHolder;
    private final AgentOption agentOption;

    private volatile AgentStatus agentStatus;

    private final InterceptorRegistryBinder interceptorRegistryBinder;
    private final ServiceTypeRegistryService serviceTypeRegistryService;
    
    private final Instrumentation instrumentation;
    private final InstrumentClassPool classPool;
    private final DynamicTransformService dynamicTransformService;
    private final List<DefaultProfilerPluginContext> pluginContexts;
    

    static {
    	System.out.println("====> ClassPreLoader.preload() before");
        // Preload classes related to ELAgent-rpc module.
        ClassPreLoader.preload();
        System.out.println("====> ClassPreLoader.preload() after");
    }
    public DefaultAgent(AgentOption agentOption) {
        this(agentOption, createInterceptorRegistry(agentOption));
    }

    public static InterceptorRegistryBinder createInterceptorRegistry(AgentOption agentOption) {
        final int interceptorSize = getInterceptorSize(agentOption);
        return new DefaultInterceptorRegistryBinder(interceptorSize);
    }

    private static int getInterceptorSize(AgentOption agentOption) {
        if (agentOption == null) {
            return DefaultInterceptorRegistryBinder.DEFAULT_MAX;
        }
        final ProfilerConfig profilerConfig = agentOption.getProfilerConfig();
        return profilerConfig.getInterceptorRegistrySize();
    }

    public DefaultAgent(AgentOption agentOption, final InterceptorRegistryBinder interceptorRegistryBinder) {
        if (agentOption == null) {
            throw new NullPointerException("agentOption must not be null");
        }
        if (agentOption.getInstrumentation() == null) {
            throw new NullPointerException("instrumentation must not be null");
        }
        if (agentOption.getProfilerConfig() == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }
        if (agentOption.getServiceTypeRegistryService() == null) {
            throw new NullPointerException("serviceTypeRegistryService must not be null");
        }

        if (interceptorRegistryBinder == null) {
            throw new NullPointerException("interceptorRegistryBinder must not be null");
        }
        logger.info("AgentOption:{}", agentOption);

        this.binder = new Slf4jLoggerBinder();
        bindPLoggerFactory(this.binder);

        this.interceptorRegistryBinder = interceptorRegistryBinder;
        interceptorRegistryBinder.bind();
        this.serviceTypeRegistryService = agentOption.getServiceTypeRegistryService();

        dumpSystemProperties();
        dumpConfig(agentOption.getProfilerConfig());

        changeStatus(AgentStatus.INITIALIZING);
        
        this.profilerConfig = agentOption.getProfilerConfig();
        this.instrumentation = agentOption.getInstrumentation();
        this.agentOption = agentOption;

        this.classPool = createInstrumentEngine(agentOption, interceptorRegistryBinder);

        if (logger.isInfoEnabled()) {
            logger.info("DefaultAgent classLoader:{}", this.getClass().getClassLoader());
        }

        pluginContexts = loadPlugins(agentOption);

        this.classFileTransformer = new ClassFileTransformerDispatcher(this, pluginContexts);
        this.dynamicTransformService = new DynamicTransformService(instrumentation, classFileTransformer);

        ClassFileTransformer wrappedTransformer = wrapClassFileTransformer(classFileTransformer);
        instrumentation.addTransformer(wrappedTransformer, true);

        String applicationServerTypeString = profilerConfig.getApplicationServerType();
        ServiceType applicationServerType = this.serviceTypeRegistryService.findServiceTypeByName(applicationServerTypeString);

        final ApplicationServerTypeResolver typeResolver = new ApplicationServerTypeResolver(pluginContexts, applicationServerType, profilerConfig.getApplicationTypeDetectOrder());
        
        final AgentInformationFactory agentInformationFactory = new AgentInformationFactory(agentOption.getAgentId(), agentOption.getApplicationName());
        this.agentInformation = agentInformationFactory.createAgentInformation(typeResolver.resolve());
        logger.info("agentInformation:{}", agentInformation);
        
        this.serverMetaDataHolder = createServerMetaDataHolder();

        this.spanDataSender = createUdpSpanDataSender(this.profilerConfig.getCollectorSpanServerPort(), "Pinpoint-UdpSpanDataExecutor",
                this.profilerConfig.getSpanDataSenderWriteQueueSize(), this.profilerConfig.getSpanDataSenderSocketTimeout(),
                this.profilerConfig.getSpanDataSenderSocketSendBufferSize());
        this.statDataSender = createUdpStatDataSender(this.profilerConfig.getCollectorStatServerPort(), "Pinpoint-UdpStatDataExecutor",
                this.profilerConfig.getStatDataSenderWriteQueueSize(), this.profilerConfig.getStatDataSenderSocketTimeout(),
                this.profilerConfig.getStatDataSenderSocketSendBufferSize());

        DefaultTraceContext defaultTraceContext = createTraceContext();

        CommandDispatcher commandService = createCommandService(defaultTraceContext);
        this.tcpDataSender = createTcpDataSender(commandService);

        defaultTraceContext.setPriorityDataSender(this.tcpDataSender);
        this.traceContext = defaultTraceContext;

        AgentStatCollectorFactory agentStatCollectorFactory = new AgentStatCollectorFactory(this.traceContext);

        JvmInformationFactory jvmInformationFactory = new JvmInformationFactory(agentStatCollectorFactory.getGarbageCollector());

        this.agentInfoSender = new AgentInfoSender.Builder(tcpDataSender, this.agentInformation, jvmInformationFactory.createJvmInformation()).sendInterval(profilerConfig.getAgentInfoSendRetryInterval()).build();
        this.serverMetaDataHolder.addListener(this.agentInfoSender);
        this.agentStatMonitor = new AgentStatMonitor(this.statDataSender, this.agentInformation.getAgentId(), this.agentInformation.getStartTime(), agentStatCollectorFactory);
        
        InterceptorInvokerHelper.setPropagateException(profilerConfig.isPropagateInterceptorException());
    }

    private InstrumentClassPool createInstrumentEngine(AgentOption agentOption, InterceptorRegistryBinder interceptorRegistryBinder) {

        final String instrumentEngine = this.profilerConfig.getProfileInstrumentEngine().toUpperCase();

        if (DefaultProfilerConfig.INSTRUMENT_ENGINE_ASM.equals(instrumentEngine)) {
            logger.info("ASM InstrumentEngine.");

            return new ASMClassPool(interceptorRegistryBinder, agentOption.getBootstrapJarPaths());

        } else if (DefaultProfilerConfig.INSTRUMENT_ENGINE_JAVASSIST.equals(instrumentEngine)) {
            logger.info("JAVASSIST InstrumentEngine.");

            return new JavassistClassPool(interceptorRegistryBinder, agentOption.getBootstrapJarPaths());
        } else {
            logger.warn("Unknown InstrumentEngine:{}", instrumentEngine);

            throw new IllegalArgumentException("Unknown InstrumentEngine:" + instrumentEngine);
        }
    }

    private ClassFileTransformer wrapClassFileTransformer(ClassFileTransformer classFileTransformerDispatcher) {
        final boolean enableBytecodeDump = profilerConfig.readBoolean(ASMBytecodeDumpService.ENABLE_BYTECODE_DUMP, ASMBytecodeDumpService.ENABLE_BYTECODE_DUMP_DEFAULT_VALUE);
        if (enableBytecodeDump) {
            logger.info("wrapBytecodeDumpTransformer");
            return BytecodeDumpTransformer.wrap(classFileTransformerDispatcher, profilerConfig);
        }
        return classFileTransformerDispatcher;
    }

    public List<String> getBootstrapJarPaths() {
        return agentOption.getBootstrapJarPaths();
    }

    protected List<DefaultProfilerPluginContext> loadPlugins(AgentOption agentOption) {
        final ProfilerPluginLoader loader = new ProfilerPluginLoader(this);
        return loader.load(agentOption.getPluginJars());
    }

    private CommandDispatcher createCommandService(TraceContext traceContext) {
        ProfilerCommandLocatorBuilder builder = new ProfilerCommandLocatorBuilder();
        builder.addService(new EchoService());
        if (traceContext instanceof DefaultTraceContext) {
            ActiveTraceLocator activeTraceLocator = ((DefaultTraceContext) traceContext).getActiveTraceLocator();
            if (activeTraceLocator != null) {
                ActiveThreadService activeThreadService = new ActiveThreadService(activeTraceLocator, traceContext.getProfilerConfig());
                builder.addService(activeThreadService);
            }
        }

        CommandDispatcher commandDispatcher = new CommandDispatcher(builder.build());
        return commandDispatcher;
    }
    
    private TransactionCounter getTransactionCounter(TraceContext traceContext) {
        if (traceContext instanceof DefaultTraceContext) {
            return ((DefaultTraceContext) traceContext).getTransactionCounter();
        }
        return null;
    }
    
    public DynamicTransformService getDynamicTransformService() {
        return dynamicTransformService;
    }

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public ClassFileTransformerDispatcher getClassFileTransformerDispatcher() {
        return classFileTransformer;
    }
    
    public InstrumentClassPool getClassPool() {
        return classPool;
    }

    private void dumpSystemProperties() {
        if (logger.isInfoEnabled()) {
            Properties properties = System.getProperties();
            Set<String> strings = properties.stringPropertyNames();
            for (String key : strings) {
                logger.info("SystemProperties {}={}", key, properties.get(key));
            }
        }
    }

    private void dumpConfig(ProfilerConfig profilerConfig) {
        if (logger.isInfoEnabled()) {
            logger.info("{}\n{}", "dumpConfig", profilerConfig);

        }
    }

    public ProfilerConfig getProfilerConfig() {
        return profilerConfig;
    }

    private void changeStatus(AgentStatus status) {
        this.agentStatus = status;
        if (logger.isDebugEnabled()) {
            logger.debug("Agent status is changed. {}", status);
        }
    }

    private void bindPLoggerFactory(PLoggerBinder binder) {
        final String binderClassName = binder.getClass().getName();
        PLogger pLogger = binder.getLogger(binder.getClass().getName());
        pLogger.info("PLoggerFactory.initialize() bind:{} cl:{}", binderClassName, binder.getClass().getClassLoader());
        // Set binder to static LoggerFactory
        // Should we unset binder at shutdown hook or stop()?
        PLoggerFactory.initialize(binder);
    }

    private DefaultTraceContext createTraceContext() {
        final StorageFactory storageFactory = createStorageFactory();
        logger.info("StorageFactoryType:{}", storageFactory);

        final Sampler sampler = createSampler();
        logger.info("SamplerType:{}", sampler);
        
        final int jdbcSqlCacheSize = profilerConfig.getJdbcSqlCacheSize();
        final boolean traceActiveThread = profilerConfig.isTraceAgentActiveThread();
        final boolean traceDataSource = profilerConfig.isTraceAgentDataSource();
        final DefaultTraceContext traceContext = new DefaultTraceContext(jdbcSqlCacheSize, this.agentInformation, storageFactory, sampler, this.serverMetaDataHolder, traceActiveThread, traceDataSource);
        traceContext.setProfilerConfig(profilerConfig);

        return traceContext;
    }

    protected StorageFactory createStorageFactory() {
        if (profilerConfig.isIoBufferingEnable()) {
            return new BufferedStorageFactory(this.spanDataSender, this.profilerConfig, this.agentInformation);
        } else {
            return new SpanStorageFactory(spanDataSender);

        }
    }

    private Sampler createSampler() {
        boolean samplingEnable = this.profilerConfig.isSamplingEnable();
        int samplingRate = this.profilerConfig.getSamplingRate();

        SamplerFactory samplerFactory = new SamplerFactory();
        return samplerFactory.createSampler(samplingEnable, samplingRate);
    }
    
    protected ServerMetaDataHolder createServerMetaDataHolder() {
        List<String> vmArgs = RuntimeMXBeanUtils.getVmArgs();
        ServerMetaDataHolder serverMetaDataHolder = new DefaultServerMetaDataHolder(vmArgs);
        return serverMetaDataHolder;
    }

    protected ELAgentClientFactory createELAgentClientFactory(CommandDispatcher commandDispatcher) {
        ELAgentClientFactory pinpointClientFactory = new ELAgentClientFactory();
        pinpointClientFactory.setTimeoutMillis(1000 * 5);

        Map<String, Object> properties = this.agentInformation.toMap();
        
        boolean isSupportServerMode = this.profilerConfig.isTcpDataSenderCommandAcceptEnable();
        
        if (isSupportServerMode) {
            pinpointClientFactory.setMessageListener(commandDispatcher);
            pinpointClientFactory.setServerStreamChannelMessageListener(commandDispatcher);

            properties.put(HandshakePropertyType.SUPPORT_SERVER.getName(), true);
            properties.put(HandshakePropertyType.SUPPORT_COMMAND_LIST.getName(), commandDispatcher.getRegisteredCommandServiceCodes());
        } else {
            properties.put(HandshakePropertyType.SUPPORT_SERVER.getName(), false);
        }

        pinpointClientFactory.setProperties(properties);
        return pinpointClientFactory;
    }

    protected EnhancedDataSender createTcpDataSender(CommandDispatcher commandDispatcher) {
        this.clientFactory = createELAgentClientFactory(commandDispatcher);
        this.client = ClientFactoryUtils.createELAgentClient(this.profilerConfig.getCollectorTcpServerIp(), this.profilerConfig.getCollectorTcpServerPort(), clientFactory);
        return new TcpDataSender(client);
    }

    protected DataSender createUdpStatDataSender(int port, String threadName, int writeQueueSize, int timeout, int sendBufferSize) {
        UdpDataSenderFactory factory = new UdpDataSenderFactory(this.profilerConfig.getCollectorStatServerIp(), port, threadName, writeQueueSize, timeout, sendBufferSize);
        return factory.create(profilerConfig.getStatDataSenderSocketType());
    }
    
    protected DataSender createUdpSpanDataSender(int port, String threadName, int writeQueueSize, int timeout, int sendBufferSize) {
        UdpDataSenderFactory factory = new UdpDataSenderFactory(this.profilerConfig.getCollectorSpanServerIp(), port, threadName, writeQueueSize, timeout, sendBufferSize);
        return factory.create(profilerConfig.getSpanDataSenderSocketType());
    }

    protected EnhancedDataSender getTcpDataSender() {
        return tcpDataSender;
    }

    protected DataSender getStatDataSender() {
        return statDataSender;
    }

    protected DataSender getSpanDataSender() {
        return spanDataSender;
    }

    public TraceContext getTraceContext() {
        return traceContext;
    }

    public AgentInformation getAgentInformation() {
        return agentInformation;
    }
    
    public ServiceTypeRegistryService getServiceTypeRegistryService() {
        return serviceTypeRegistryService;
    }
    
    @Override
    public void start() {
        synchronized (this) {
            if (this.agentStatus == AgentStatus.INITIALIZING) {
                changeStatus(AgentStatus.RUNNING);
            } else {
                logger.warn("Agent already started.");
                return;
            }
        }
        logger.info("Starting {} Agent.", ProductInfo.NAME);
        this.agentInfoSender.start();
        this.agentStatMonitor.start();
    }

    @Override
    public void stop() {
        stop(false);
    }

    public void stop(boolean staticResourceCleanup) {
        synchronized (this) {
            if (this.agentStatus == AgentStatus.RUNNING) {
                changeStatus(AgentStatus.STOPPED);
            } else {
                logger.warn("Cannot stop agent. Current status = [{}]", this.agentStatus);
                return;
            }
        }
        logger.info("Stopping {} Agent.", ProductInfo.NAME);

        this.agentInfoSender.stop();
        this.agentStatMonitor.stop();

        // Need to process stop
        this.spanDataSender.stop();
        this.statDataSender.stop();

        closeTcpDataSender();
        // for testcase
        if (staticResourceCleanup) {
            PLoggerFactory.unregister(this.binder);
            this.interceptorRegistryBinder.unbind();
        }
    }

    private void closeTcpDataSender() {
        if (this.tcpDataSender != null) {
            this.tcpDataSender.stop();
        }
        if (this.client != null) {
            this.client.close();
        }
        if (this.clientFactory != null) {
            this.clientFactory.release();
        }
    }

}
