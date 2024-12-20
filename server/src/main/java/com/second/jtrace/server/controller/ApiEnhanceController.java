package com.second.jtrace.server.controller;


import com.second.jtrace.core.command.enhance.ResetCommand;
import com.second.jtrace.core.command.enhance.StackCommand;
import com.second.jtrace.core.command.enhance.TraceCommand;
import com.second.jtrace.core.command.enhance.WatchCommand;
import com.second.jtrace.core.command.enhance.response.ResetResponse;
import com.second.jtrace.core.command.enhance.response.StackResponse;
import com.second.jtrace.core.command.enhance.response.TraceResponse;
import com.second.jtrace.core.command.enhance.response.WatchResponse;
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
@RequestMapping("api/enhance")
public class ApiEnhanceController {
    @Autowired
    private JTraceServer server;

    @RequestMapping("trace")
    public Result trace(@RequestParam String clientId
            , @RequestBody TraceCommand command) {
        TraceResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("watch")
    public Result watch(@RequestParam String clientId
            , @RequestBody WatchCommand command) {
        WatchResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("stack")
    public Result stack(@RequestParam String clientId, @RequestBody StackCommand command) {
        StackResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("reset")
    public Result reset(@RequestParam String clientId, @RequestBody ResetCommand command) {
        ResetResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }
}
