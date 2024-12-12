package netty;

import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<IMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    JTraceServer server;
    public ServerHandler(JTraceServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立时
        Channel channel = ctx.channel();
        String clientID
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 连接断开时
        Channel channel = ctx.channel();

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Channel exception:", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMessage message) throws Exception {

    }
}
