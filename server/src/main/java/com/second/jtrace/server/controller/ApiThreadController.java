package com.second.jtrace.server.controller;


import com.second.jtrace.core.command.thread.ThreadAllCommand;
import com.second.jtrace.core.command.thread.response.ThreadAllResponse;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.server.dto.Result;
import com.second.jtrace.server.netty.JTraceServer;
import com.second.jtrace.server.util.CommandUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/thread")
public class ApiThreadController {
    @Autowired
    private JTraceServer server;

    @RequestMapping("all")
    public Result all(@RequestParam String clientId, @RequestBody ThreadAllCommand command) {
        ThreadAllResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }
}
