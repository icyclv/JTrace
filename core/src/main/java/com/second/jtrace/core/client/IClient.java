package com.second.jtrace.core.client;

import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IResponse;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;


public interface IClient {
    Instrumentation getInstrumentation();

    String getClientId();

    void setClientId(String clientId);
    String getClientName();

    void write(ICommand command, IResponse response);

    void write(IMessage message);

    void destroy();

    void enableSampling(String className,int sampleInterval, int reportInterval)

    EnhancerAffect reset() throws UnmodifiableClassException;
}
