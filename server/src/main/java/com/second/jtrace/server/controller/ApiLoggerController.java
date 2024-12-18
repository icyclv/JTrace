package com.second.jtrace.server.controller;


import com.second.jtrace.core.command.logger.LoggerInfoCommand;
import com.second.jtrace.core.command.logger.LoggerLevelCommand;
import com.second.jtrace.core.command.logger.response.LoggerInfoResponse;
import com.second.jtrace.core.command.logger.response.LoggerLevelResponse;
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
@RequestMapping("api/logger")
public class ApiLoggerController {
    @Autowired
    private JTraceServer server;

    @RequestMapping("info")
    public Result detail(@RequestParam String clientId, @RequestBody LoggerInfoCommand command) {
        LoggerInfoResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("level")
    public Result detail(@RequestParam String clientId, @RequestBody LoggerLevelCommand command) {
        LoggerLevelResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }
}
