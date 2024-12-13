package com.second.jtrace.core.client;

import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.response.IResponse;

import java.lang.instrument.Instrumentation;


public interface IClient {
    Instrumentation getInstrumentation();

    String getClientId();

    void setClientId(String clientId);
    String getClientName();

    void write(ICommand command, IResponse response);
}
