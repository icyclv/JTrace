package com.second.jtrace.core.command.client;

import com.alibaba.bytekit.utils.IOUtils;
import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.common.SystemInfoUtil;
import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.client.response.ClientInfoResponse;
import com.second.jtrace.core.command.client.vo.ClientInfoVO;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class ClientInfoCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(ClientInfoCommand.class);

    private String clientId;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ClientInfoResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        if(!StringUtils.isBlank(clientId)){
            client.setClientId(clientId);
        }
        String host =  SystemInfoUtil.HOST_NAME;
        String ip = SystemInfoUtil.LOCAL_IP_ADDRESS;

        ClientInfoVO clientInfo = new ClientInfoVO();
        clientInfo.setHost(host);
        clientInfo.setIp(ip);
        clientInfo.setVersion(getVersion());



        clientInfo.setClientName(client.getClientName());
        clientInfo.setClientId(clientId);

        ClientInfoResponse clientInfoResponse = new ClientInfoResponse();
        clientInfoResponse.setClientInfo(clientInfo);
        return clientInfoResponse;
    }

    private String getVersion() {
        StringBuilder version = new StringBuilder();
        for(int i=0;i<JTraceConstants.VERSION.length;i++){
            version.append(JTraceConstants.VERSION[i]);
            if(i != JTraceConstants.VERSION.length - 1){
                version.append(".");
            }
        }
        return version.toString();
    }

    @Override
    public int getMessageTypeId() {
        return 0;
    }
}
