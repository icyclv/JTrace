package com.second.jtrace.core.client;

import com.second.jtrace.core.protocol.MessageCodec;
import com.second.jtrace.core.protocol.PingMessage;
import com.second.jtrace.core.protocol.ProcotolFrameDecoder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class JTraceClientInitializer extends ChannelInitializer<SocketChannel> {
    private JTraceClient jTraceClient;
    MessageCodec MESSAGE_CODEC = new MessageCodec();
    LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.WARN);



    public JTraceClientInitializer(JTraceClient jTraceClient) {
        this.jTraceClient = jTraceClient;

    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast( new ProcotolFrameDecoder());
        ch.pipeline().addLast(LOGGING_HANDLER);
        ch.pipeline().addLast(MESSAGE_CODEC);
        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0));
        ch.pipeline().addLast(new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(new PingMessage());
                }
            }
        });
        ch.pipeline().addLast(new JTraceClientHandler(jTraceClient));




    }
}
