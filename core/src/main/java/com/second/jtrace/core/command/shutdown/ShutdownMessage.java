package com.second.jtrace.core.command.shutdown;

import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.protocol.IMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownMessage implements IMessage {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownMessage.class);


    public void execute(IClient client) {
        try {
            client.reset();
        } catch (Exception e) {
            logger.error("ShutdownCommand execute error", e);
        } finally {
            client.destroy();
        }
    }


}
