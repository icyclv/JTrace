package com.second.jtrace.core.command.jvm;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.jvm.response.SysEnvResponse;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.IResponse;


public class SysEnvCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        SysEnvResponse response = new SysEnvResponse();
        response.setInfo(System.getenv());
        return response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SysEnvResponse.class;
    }


}
