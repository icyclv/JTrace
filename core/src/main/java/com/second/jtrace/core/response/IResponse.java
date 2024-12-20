package com.second.jtrace.core.response;


import com.second.jtrace.core.protocol.IMessage;

public interface IResponse extends IMessage {

    String getClientId();

    void setClientId(String clientId);

    String getCommandId();

    void setCommandId(String commandId);
}
