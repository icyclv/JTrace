package com.second.jtrace.core.command;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IResponse;

public interface ICommand extends IMessage {

    String getCommandId();

    void setCommandId(String commandId);

    String getSessionId();

    void setSessionId(String sessionId);

    void execute(IClient client);

    Class<? extends IResponse> getResponseClass();
}