package com.second.jtrace.core.command.shutdown;

import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShutdownMessage implements IMessage {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownMessage.class);


    public void execute(IClient client) {
        try{
           client.reset();
        }catch (Exception e){
            logger.error("ShutdownCommand execute error",e);
        }finally {
            client.destroy();
        }
    }



}
