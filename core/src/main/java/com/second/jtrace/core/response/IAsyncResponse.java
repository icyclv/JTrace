package com.second.jtrace.core.response;


public interface IAsyncResponse extends IResponse {
    String getSessionId();

    void setSessionId(String sessionId);
}