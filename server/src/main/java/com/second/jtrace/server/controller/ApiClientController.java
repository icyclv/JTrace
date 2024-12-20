package com.second.jtrace.server.controller;

import com.second.jtrace.core.command.client.ClientInfoCommand;
import com.second.jtrace.core.command.client.response.ClientInfoResponse;
import com.second.jtrace.core.command.client.vo.ClientInfoVO;
import com.second.jtrace.core.command.shutdown.ShutdownMessage;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.server.dto.Result;
import com.second.jtrace.server.netty.ClientChannel;
import com.second.jtrace.server.netty.JTraceServer;
import com.second.jtrace.server.util.CommandUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@RequestMapping("api/client")
public class ApiClientController {
    @Autowired
    private JTraceServer server;

    @RequestMapping("list")
    public Result list() {
        Map<String, ClientChannel> clientChannels = server.getClients();
        List<ClientInfoVO> clientInfos = new ArrayList<>();
        for (ClientChannel clientChannel : clientChannels.values()) {
            clientInfos.add(clientChannel.getClientInfoVO());
        }
        Collections.sort(clientInfos, new Comparator<ClientInfoVO>() {
            @Override
            public int compare(ClientInfoVO o1, ClientInfoVO o2) {
                if (o1.getClientName().equals(o2.getClientName())) {
                    return o1.getClientId().compareTo(o2.getClientId());
                }
                return o1.getClientName().compareTo(o2.getClientName());
            }
        });
        return Result.ok(clientInfos);
    }


    @RequestMapping("info")
    public Result info(@RequestParam String clientId) {
        ClientInfoCommand command = new ClientInfoCommand();
        ClientInfoResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response.getClientInfo());
    }

    @RequestMapping("shutdown")
    public Result shutdown(@RequestParam String clientId) {
        ShutdownMessage message = new ShutdownMessage();
        BaseResponse response = server.sendMessage(clientId, message);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }

        return Result.ok();
    }
}
