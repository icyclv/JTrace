package com.second.jtrace.core.command;

import com.second.jtrace.core.client.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class CommandTask implements Callable<Boolean> {
    Logger logger = LoggerFactory.getLogger(CommandTask.class);
    ICommand command;
    IClient client;
    public CommandTask(ICommand command, IClient client) {
        this.command = command;
        this.client = client;
    }

    @Override
    public Boolean call() {
        try {
            command.execute(client);
            return true;
        } catch (Exception e) {
            logger.error("Error executing command", e);
            return false;
        }
    }
}
