package com.second.jtrace.client;




import com.second.jtrace.common.SystemInfoUtil;
import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.CommandTask;
import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.response.IAsyncResponse;
import com.second.jtrace.core.response.IResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JTraceClient implements IClient {

        private static final Logger logger = LoggerFactory.getLogger(JTraceClient.class);

        private String clientId;
        private String clientName;
        private String serverHost;
        private int serverPort;
        private Instrumentation instrumentation;

        private ScheduledExecutorService executorService;


        private EventLoopGroup group;
        private Channel channel;

        public JTraceClient(Instrumentation instrumentation, String clientName, String serverHost, int serverPort) {
            this.instrumentation = instrumentation;
            this.clientName = clientName;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.clientId = "JTraceClient-"+ SystemInfoUtil.JVM_NAME + "-" + System.currentTimeMillis();
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


    @Override
    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientName() {
        return clientName;
    }

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

        public void close() {
            if (channel != null) {
                channel.close();
            }
            channel = null;
            if (group != null) {
                shutdownWorkGroup();
            }
            group = null;
            if(executorService != null) {
                executorService.shutdown();
            }
            executorService = null;
            logger.info("JTraceClient closed");
        }

        private void shutdownWorkGroup() {
            if (group != null) {
                group.shutdownGracefully(200, 200, TimeUnit.MILLISECONDS);
                group = null;
            }
        }





}
