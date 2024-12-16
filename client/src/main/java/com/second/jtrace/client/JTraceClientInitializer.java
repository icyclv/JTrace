package com.second.jtrace.client;

import com.second.jtrace.core.protocol.MessageCodec;
import com.second.jtrace.core.protocol.PingMessage;
import com.second.jtrace.core.protocol.ProtocolFrameDecoder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class JTraceClientInitializer extends ChannelInitializer<SocketChannel> {
    private final JTraceClient client;
    private final MessageCodec MESSAGE_CODEC = new MessageCodec();
    private final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.WARN);



    public JTraceClientInitializer(JTraceClient client) {
        this.client = client;

    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast( new ProtocolFrameDecoder());
        ch.pipeline().addLast(LOGGING_HANDLER);
        ch.pipeline().addLast(MESSAGE_CODEC);
        ch.pipeline().addLast(new IdleStateHandler(0, 5, 0));
        ch.pipeline().addLast(new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                if (evt instanceof IdleStateEvent) {
                    IdleStateEvent event = (IdleStateEvent) evt;
                    handleIdleEvent(ctx, event);
                }
            }
        });
        ch.pipeline().addLast(new JTraceClientHandler(client));


    }


    private void handleIdleEvent(ChannelHandlerContext ctx, IdleStateEvent event) {
        switch (event.state()) {
            case READER_IDLE:
                ctx.close();
                break;
            case WRITER_IDLE:
                ctx.writeAndFlush(new PingMessage());
                break;
            case ALL_IDLE:
                break;
        }
    }
}
