package com.second.jtrace.server.netty;

import com.second.jtrace.core.protocol.MessageCodec;
import com.second.jtrace.core.protocol.ProtocolFrameDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

public class JTraceServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final MessageCodec MESSAGE_CODEC = new MessageCodec();
    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.WARN);
    private final JTraceServer server;
    private static final AttributeKey<String> CLIENT_ID_KEY = AttributeKey.valueOf("client_id");

    public JTraceServerInitializer(JTraceServer jTraceServer) {
        this.server = jTraceServer;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast( new ProtocolFrameDecoder());
        ch.pipeline().addLast(LOGGING_HANDLER);
        ch.pipeline().addLast(MESSAGE_CODEC);
        ch.pipeline().addLast(new IdleStateHandler(10, 0, 0));
        ch.pipeline().addLast(new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
              if(evt instanceof IdleStateEvent){
                handleIdleEvent(ctx, (IdleStateEvent) evt);
              }else {
                super.userEventTriggered(ctx, evt);
              }
            }
        });
        ch.pipeline().addLast(new JTraceServerHandler(server));




    }

    private void handleIdleEvent(ChannelHandlerContext ctx, IdleStateEvent event) {
        switch (event.state()) {
            case READER_IDLE:
                Channel channel = ctx.channel();
                String clientId = channel.attr(CLIENT_ID_KEY).get();
                    server.removeClient(clientId);
                    channel.close();
                break;
            case WRITER_IDLE:
                break;
            case ALL_IDLE:
                break;
        }
    }

}
