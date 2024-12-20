package com.second.jtrace.core.command.jvm;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.jvm.response.SysPropResponse;
import com.second.jtrace.core.response.IResponse;

import java.util.Properties;


public class SysPropCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        SysPropResponse response = new SysPropResponse();
        Properties properties = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            response.getInfo().put(key, properties.getProperty(key));
        }
        return response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SysPropResponse.class;
    }


}
