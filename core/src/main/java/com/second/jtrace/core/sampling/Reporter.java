package com.second.jtrace.core.sampling;

import com.second.jtrace.common.SystemInfoUtil;
import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.sampling.bean.SamplingMessage;

import java.util.Map;

public class Reporter {
    private final IClient client;
    private final String hostName;
    private final String ip;
    private final String processId;

    public Reporter(IClient client) {
        this.client = client;
        this.hostName = SystemInfoUtil.HOST_NAME;
        this.ip = SystemInfoUtil.LOCAL_IP_ADDRESS;
        this.processId = SystemInfoUtil.PROCESS_ID;
    }

    public void report(String profilerName, Map<String, Object> result) {

        result.put("clientName", client.getClientName());
        result.put("clientId", client.getClientId());
        result.put("hostName", hostName);
        result.put("ip", ip);
        result.put("processId", processId);
        SamplingMessage profilerResult = new SamplingMessage(profilerName, result);
        client.write(profilerResult);

    }
}
