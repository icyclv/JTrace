package com.second.jtrace.client;

import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.command.CommandTask;
import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.command.shutdown.ShutdownMessage;
import com.second.jtrace.core.protocol.IMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class JTraceClientHandler extends SimpleChannelInboundHandler<IMessage> {



    private static final Logger logger = LoggerFactory.getLogger(JTraceClientHandler.class);
    private JTraceClient client;

    public JTraceClientHandler(JTraceClient client) {
       this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage message) throws Exception {
        try {
            if(message instanceof ICommand){
                ICommand command = (ICommand) message;
                Future<Boolean> future = client.submit(new CommandTask(command, client));
                try{
                    if(future.get(JTraceConstants.TIME_OUT, TimeUnit.SECONDS)){
                        logger.info("Command {} executed successfully", command.getCommandId());
                    }
                } catch (InterruptedException e) {
                    logger.error("Command {} interrupted", command.getCommandId());
                } catch (ExecutionException e) {
                    logger.error("Command {} execution failed", command.getCommandId());
                } catch (TimeoutException e) {
                    logger.error("Command {} execution timed out", command.getCommandId());
                }
            }else if(message instanceof ShutdownMessage){
                logger.info("Shutdown command received, shutting down client");
                ((ShutdownMessage) message).execute(client);



            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception in Netty Client: {}", cause.getMessage(), cause);
        ctx.close();
        client.destroy();

    }


}
