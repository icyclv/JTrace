package com.second.jtrace.server.netty;

import com.second.jtrace.core.command.client.ClientInfoCommand;
import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.protocol.PingMessage;
import com.second.jtrace.core.response.IResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JTraceServerHandler extends SimpleChannelInboundHandler<IMessage> {
    private static final Logger logger = LoggerFactory.getLogger(JTraceServerHandler.class);
    JTraceServer server;
    public JTraceServerHandler(JTraceServer server) {
        this.server = server;
    }
    private static final AttributeKey<String> CLIENT_ID_KEY = AttributeKey.valueOf("client_id");



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立时
        Channel channel = ctx.channel();
        String clientID = "JTraceClient-" + channel.remoteAddress().toString()+ "-" + channel.id().asLongText();
        logger.info("Client connected: {}", clientID);
        channel.attr(CLIENT_ID_KEY).set(clientID);
        ClientInfoCommand clientInfoCommand = new ClientInfoCommand();
        clientInfoCommand.setClientId(clientID);
        channel.writeAndFlush(clientInfoCommand);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 连接断开时
        Channel channel = ctx.channel();
        String clientID = channel.attr(CLIENT_ID_KEY).get();
        logger.info("Client disconnected: {}", clientID);
        server.removeClient(clientID);

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String clientID = channel.attr(CLIENT_ID_KEY).get();
        logger.error("Client exception: {}", clientID, cause);
        server.removeClient(clientID);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage message) throws Exception {
        if(message instanceof IResponse) {
            if(message instanceof PingMessage){
                logger.debug("Received ping message from {}", ctx.channel().remoteAddress());
            }else {
                server.handleResponse((IResponse) message, ctx.channel());
            }
        }else {
            logger.error("Unknown message type: {}", message.getClass().getName());
        }
    }
}
