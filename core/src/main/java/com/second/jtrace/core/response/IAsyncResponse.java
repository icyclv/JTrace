package com.second.jtrace.core.response;


public interface IAsyncResponse extends IResponse {
    void setSessionId(String sessionId);

    String getSessionId();
}