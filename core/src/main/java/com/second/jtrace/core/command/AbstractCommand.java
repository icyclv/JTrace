package com.second.jtrace.core.command;

import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.core.response.IResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

    protected String commandId;

    protected String sessionId;

    @Override
    public void execute(IClient client) {
        IResponse response = null;
        try {
            response = executeForResponse(client);
            if (response != null) {
                client.write(this, response);
            } else {
                logger.warn("response is null, command: {},{}", this.getClass().getSimpleName(), commandId);
            }
        } catch (Throwable ex) {
            client.write(this, createExceptionResponse("Command error:" + ex.getMessage() + ",commandId:" + commandId));
        }
    }


    protected IResponse createExceptionResponse(String message) {
        return BaseResponse.fail(message, getResponseClass());
    }


    public abstract IResponse executeForResponse(IClient client);


    @Override
    public String getCommandId() {
        return this.commandId;
    }

    @Override
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}