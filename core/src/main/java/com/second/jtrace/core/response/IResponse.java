package com.second.jtrace.core.response;


import com.second.jtrace.core.protocol.IMessage;

public interface IResponse extends IMessage {

    void setClientId(String clientId);

    String getClientId();

    void setCommandId(String commandId);

    String getCommandId();
}
