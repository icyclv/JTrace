package com.second.jtrace.core.client;

import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.sampling.profiler.Profiler;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.concurrent.ConcurrentHashMap;


public interface IClient {
    Instrumentation getInstrumentation();

    String getClientId();

    void setClientId(String clientId);
    String getClientName();

    void write(ICommand command, IResponse response);

    void write(IMessage message);

    void destroy();

    boolean enableSampling(String profilerName,int sampleInterval, int reportInterval);
    boolean disableSampling(String profilerName);
    ConcurrentHashMap<String, Profiler> getProfilers();

    EnhancerAffect reset() throws UnmodifiableClassException;
}
