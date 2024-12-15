package com.second.jtrace.core.response;

public class AbstractAsyncResponse extends BaseResponse implements IAsyncResponse {
    protected String sessionId;

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }


}
