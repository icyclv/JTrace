package com.second.jtrace.server.netty;


import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.command.client.response.ClientInfoResponse;
import com.second.jtrace.core.command.client.vo.ClientInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.core.response.IAsyncResponse;
import com.second.jtrace.core.response.IResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JTraceServer {
    private static final Logger logger = LoggerFactory.getLogger(JTraceServer.class);

    private int port;

    /**
     * 连接的客户端列表
     */
    private Map<String, ClientChannel> clientChannels = new ConcurrentHashMap<>();

    /**
     * 所有活跃的Channel
     */
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public JTraceServer(int port) {
        this.port = port;
    }

    /**
     * 服务端启动
     */
    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new JTraceServerInitializer(this))
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("Netty Server started on port: {}", port);
        }catch (Exception e){
            logger.error("Netty Server start error", e);
        }

    }

    /**
     * 添加客户端连接
     *
     * @param channel    客户端连接
     * @param clientInfo 客户端信息
     */
    public synchronized void addClient(Channel channel, ClientInfoVO clientInfo) {


        ClientChannel clientChannel = new ClientChannel();

        clientChannel.setClientInfoVO(clientInfo);
        clientChannel.setChannel(channel);
        clientChannel.setEyeServer(this);
        clientChannels.put(clientInfo.getClientId(), clientChannel);
    }

    /**
     * 发送命令
     *
     * @param clientId 客户端ID
     * @param command  命令
     * @return
     */
    public IResponse sendCommand(String clientId, ICommand command) {
        ClientChannel clientChannel = clientChannels.get(clientId);
        if (clientChannel != null) {
            return clientChannel.sendCommand(command);
        }
        return BaseResponse.fail("clientId 不存在：" + clientId, command.getResponseClass());
    }

    /**
     * 异步返回的消息处理，通过Websocket转发到前端
     *
     * @param response 异步返回消息
     */
    public void asyncResponse(IAsyncResponse response) {
//        WsServerEndpoint.send(this, response);
    }

    /**
     * 移除客户连接
     *
     * @param clientId 客户端Id
     */
    public void removeClient(String clientId) {
        clientChannels.remove(clientId);
    }

    /**
     * 获取连接的客户端列表
     *
     * @return
     */
    public Map<String, ClientChannel> getClients() {
        return clientChannels;
    }

    /**
     * 根据客户端ID获取客户端连接
     *
     * @param clientId 客户端ID
     * @return
     */
    public ClientChannel getClient(String clientId) {
        return clientChannels.get(clientId);
    }

    /**
     * 返回结果处理
     *
     * @param response 返回结果
     * @param channel  客户端连接
     */
    public void handleResponse(IResponse response, Channel channel) {
        if (response instanceof ClientInfoResponse) {
            addClient(channel, ((ClientInfoResponse) response).getClientInfo());
            return;
        }
        if (response instanceof IAsyncResponse) {
            asyncResponse((IAsyncResponse) response);
        } else {
            ClientChannel clientChannel = getClient(response.getClientId());
            if (clientChannel != null) {
                clientChannel.handleResponse(response);
            } else {
                logger.warn("clientChannel is null：" + response);
            }
        }
    }

    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

    }

}
