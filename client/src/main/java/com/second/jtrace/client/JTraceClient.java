package com.second.jtrace.client;




import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.CommandTask;
import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.enhance.EnhanceManager;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IAsyncResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.sampling.Reporter;
import com.second.jtrace.core.sampling.bean.StacktraceMetricBuffer;
import com.second.jtrace.core.sampling.profiler.*;
import com.second.jtrace.spy.SpyAPI;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

public class JTraceClient implements IClient {

        private static final Logger logger = LoggerFactory.getLogger(JTraceClient.class);

        private String clientId;
        private final String clientName;
        private final String serverHost;
        private final int serverPort;
        private final Instrumentation instrumentation;

        private ScheduledExecutorService executorService;

        private Thread shutdown;

        private ScheduledExecutorService samplingService;
        private Reporter reporter;
        private ConcurrentHashMap<String, ScheduledFuture> profilerScheduler = new ConcurrentHashMap<>();
        private ConcurrentHashMap<String, Profiler> profilers = new ConcurrentHashMap<>();
        private EventLoopGroup group;
        private Channel channel;
        private EnhanceManager enhanceManager;
        public JTraceClient(Instrumentation instrumentation, String clientName, String serverHost, int serverPort) {
            this.instrumentation = instrumentation;
            this.clientName = clientName;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            initSpy();
            // 初始化 Netty 客户端
            executorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                private AtomicInteger seq = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    final Thread t = new Thread(r, "jtrace-command-execute"+seq.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            });

            samplingService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                private AtomicInteger seq = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    final Thread t = new Thread(r, "jtrace-sampling"+seq.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            });
            reporter = new Reporter(this);

            EnhanceManager.init(instrumentation);

            startNettyClient();


        }

    private void startNettyClient() {
        executorService.scheduleWithFixedDelay(() -> {
            if (channel != null && channel.isActive()) {
                return;
            }

            logger.info("Attempting to connect to server at {}:{}", serverHost, serverPort);
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new JTraceClientInitializer(this));

            try {
                ChannelFuture future = bootstrap.connect(serverHost, serverPort).sync();
                channel = future.channel();
                logger.info("Connected to server at {}:{}", serverHost, serverPort);
            } catch (Exception e) {
                logger.error("Failed to connect to server: {}. Retrying in 5 seconds...", e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public Future<Boolean> submit(CommandTask task) {
            return executorService.submit(task);
        }

    private void initSpy() {

        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        Class<?> spyClass = null;
        if (parent != null) {
            try {
                spyClass =parent.loadClass("com.second.jtrace.spy.SpyAPI");
            } catch (Throwable e) {
                // ignore
            }
        }
        if(spyClass == null){
            try {
                //TODO: need dynamic path
                File jarFlie = new File("E:\\codespace\\java\\JTrace\\spy\\target\\jtrace-spy.jar");
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(jarFlie));
                System.out.println(1);
            } catch (Throwable e) {
                throw new IllegalStateException("Failed to load jtrace-spy.jar", e);
            }
        }

    }

    @Override
    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientName() {
        return clientName;
    }

    @Override
    public void write(ICommand command, IResponse response) {
            response.setClientId(clientId);
            response.setCommandId(command.getCommandId());
            if (response instanceof IAsyncResponse) {
                ((IAsyncResponse) response).setSessionId(command.getSessionId());
            }
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(response);
            } else {
                logger.warn("Channel is not active. Failed to send response: {}", response.getCommandId());
            }
    }
    @Override
    public void write(IMessage message) {


        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        } else {
            logger.warn("Channel is not active. Failed to send message: {}", message);
        }
    }



    private void shutdownWorkGroup() {
        if (group != null) {
            group.shutdownGracefully(200, 200, TimeUnit.MILLISECONDS);
            group = null;
        }
    }

    public void destroy() {

        if (channel != null) {
            channel.close();
        }
        channel = null;
        if (group != null) {
            shutdownWorkGroup();
        }
        group = null;
        if(executorService != null) {
            executorService.shutdownNow();
        }
        executorService = null;

        if(samplingService != null) {
            samplingService.shutdownNow();
        }

        EnhanceManager.destroy(instrumentation);
        cleanUpSpyReference();
        logger.info("JTraceClient closed");
    }

    @Override
    public synchronized boolean enableSampling(String profilerName, int sampleInterval, int reportInterval) {
       switch (profilerName){
           case "Stacktrace":
               if(profilerScheduler.containsKey("StacktraceReporter") || profilerScheduler.containsKey("StacktraceCollector")){
                   // 更新采样间隔
                     if(profilerScheduler.containsKey("StacktraceReporter")){
                          ScheduledFuture<?> stacktraceReporterFuture = profilerScheduler.get("StacktraceReporter");
                          StacktraceReporterProfiler stacktraceReporterProfiler = (StacktraceReporterProfiler) profilers.get("StacktraceReporter");
                          stacktraceReporterProfiler.setIntervalMillis(reportInterval);
                          stacktraceReporterFuture.cancel(false);
                          stacktraceReporterFuture = samplingService.scheduleAtFixedRate(stacktraceReporterProfiler::profile, 0, stacktraceReporterProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
                          profilerScheduler.put("StacktraceReporter", stacktraceReporterFuture);
                     }
                     if(profilerScheduler.containsKey("StacktraceCollector")){
                         ScheduledFuture<?> stacktraceCollectorFuture = profilerScheduler.get("StacktraceCollector");
                         StacktraceCollectorProfiler stacktraceCollectorProfiler = (StacktraceCollectorProfiler) profilers.get("StacktraceCollector");
                         stacktraceCollectorProfiler.setIntervalMillis(sampleInterval);
                         stacktraceCollectorFuture.cancel(false);
                         stacktraceCollectorFuture = samplingService.scheduleAtFixedRate(stacktraceCollectorProfiler::profile, 0, stacktraceCollectorProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
                         profilerScheduler.put("StacktraceCollector", stacktraceCollectorFuture);
                     }
                     break;

               }
               StacktraceMetricBuffer stacktraceMetricBuffer = new StacktraceMetricBuffer();

               StacktraceCollectorProfiler stacktraceCollectorProfiler = new StacktraceCollectorProfiler(stacktraceMetricBuffer, "jtrace");
               stacktraceCollectorProfiler.setIntervalMillis(sampleInterval);

                StacktraceReporterProfiler stacktraceReporterProfiler = new StacktraceReporterProfiler(stacktraceMetricBuffer, reporter);
                stacktraceReporterProfiler.setIntervalMillis(reportInterval);
               ScheduledFuture<?> stacktraceCollectorFuture = samplingService.scheduleAtFixedRate(stacktraceCollectorProfiler::profile, 0, stacktraceCollectorProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);

               ScheduledFuture<?> stacktraceReportFuture = samplingService.scheduleAtFixedRate(stacktraceReporterProfiler::profile, 0, stacktraceReporterProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
               profilerScheduler.put("StacktraceReporter", stacktraceReportFuture);
               profilerScheduler.put("StacktraceCollector", stacktraceCollectorFuture);
                profilers.put("StacktraceReporter", stacktraceReporterProfiler);
                profilers.put("StacktraceCollector", stacktraceCollectorProfiler);
               break;
           case "ThreadInfo":
               if(profilers.containsKey("ThreadInfo")){
                   ScheduledFuture<?> threadInfoFuture = profilerScheduler.get("ThreadInfo");
                     ThreadInfoProfiler threadInfoProfiler = (ThreadInfoProfiler) profilers.get("ThreadInfo");
                        threadInfoProfiler.setIntervalMillis(reportInterval);
                        threadInfoFuture.cancel(false);
                        threadInfoFuture = samplingService.scheduleAtFixedRate(threadInfoProfiler::profile, 0, threadInfoProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
                        profilerScheduler.put("ThreadInfo", threadInfoFuture);
                     break;
               }
               ThreadInfoProfiler threadInfoProfiler = new ThreadInfoProfiler(reporter);
                threadInfoProfiler.setIntervalMillis(reportInterval);
               ScheduledFuture<?> future = samplingService.scheduleAtFixedRate(threadInfoProfiler::profile, 0, threadInfoProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
               profilers.put("ThreadInfo", threadInfoProfiler);
                profilerScheduler.put("ThreadInfo", future);

               break;
           case "CpuAndMemory":
                if(profilers.containsKey("CpuAndMemory")){
                    ScheduledFuture<?> cpuAndMemoryFuture = profilerScheduler.get("CpuAndMemory");
                    CpuAndMemoryProfiler cpuAndMemoryProfiler = (CpuAndMemoryProfiler) profilers.get("CpuAndMemory");
                    cpuAndMemoryProfiler.setIntervalMillis(reportInterval);
                    cpuAndMemoryFuture.cancel(false);
                    cpuAndMemoryFuture = samplingService.scheduleAtFixedRate(cpuAndMemoryProfiler::profile, 0, cpuAndMemoryProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
                    profilerScheduler.put("CpuAndMemory", cpuAndMemoryFuture);
                    break;

                }
               CpuAndMemoryProfiler cpuAndMemoryProfiler = new CpuAndMemoryProfiler(reporter);
                cpuAndMemoryProfiler.setIntervalMillis(reportInterval);
               ScheduledFuture<?> cpuAndMemoryProfilerFuture = samplingService.scheduleAtFixedRate(cpuAndMemoryProfiler::profile, 0, cpuAndMemoryProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
               profilers.put("CpuAndMemory", cpuAndMemoryProfiler);
                profilerScheduler.put("CpuAndMemory", cpuAndMemoryProfilerFuture);
               break;
           case "IO":
               if(profilers.containsKey("IO")){
                   ScheduledFuture<?> ioFuture = profilerScheduler.get("IO");
                     IOProfiler ioProfiler = (IOProfiler) profilers.get("IO");
                        ioProfiler.setIntervalMillis(reportInterval);
                        ioFuture.cancel(false);
                        ioFuture = samplingService.scheduleAtFixedRate(ioProfiler::profile, 0, ioProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
                        profilerScheduler.put("IO", ioFuture);
                        break;
               }
                IOProfiler ioProfiler = new IOProfiler(reporter);
                ioProfiler.setIntervalMillis(reportInterval);
               ScheduledFuture<?> ioProfilerFuture = samplingService.scheduleAtFixedRate(ioProfiler::profile, 0, ioProfiler.getIntervalMillis(), TimeUnit.MILLISECONDS);
                profilers.put("IO", ioProfiler);
                 profilerScheduler.put("IO", ioProfilerFuture);
                 break;
           default:
               logger.warn("Unknown profiler: {}", profilerName);
               return false;
       }
       return true;
    }

    @Override
    public boolean disableSampling(String profilerName) {
        if(profilerScheduler.containsKey(profilerName)){
            ScheduledFuture<?> future = profilerScheduler.get(profilerName);
            future.cancel(false);
            profilerScheduler.remove(profilerName);
            profilers.remove(profilerName);
            return true;
        }
        return false;
    }

    @Override
    public ConcurrentHashMap<String, Profiler> getProfilers() {
        return this.profilers;
    }


    @Override
    public EnhancerAffect reset() throws UnmodifiableClassException {
            return EnhanceManager.reset(instrumentation);
    }



    private void cleanUpSpyReference() {
        try {
            SpyAPI.setNopSpy();
            SpyAPI.destroy();
        } catch (Throwable e) {
            // ignore
        }
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("com.second.jtrace.agent.JTraceAgent");
            Method method = clazz.getDeclaredMethod("resetClassLoader");
            method.invoke(null);
        } catch (Throwable e) {
            // ignore
        }
    }






}
