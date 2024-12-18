package com.second.jtrace.server.controller;


import com.second.jtrace.core.command.clazz.ClassCommand;
import com.second.jtrace.core.command.clazz.JadCommand;
import com.second.jtrace.core.command.clazz.MethodCommand;
import com.second.jtrace.core.command.clazz.response.ClassResponse;
import com.second.jtrace.core.command.clazz.response.JadResponse;
import com.second.jtrace.core.command.clazz.response.MethodResponse;
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
@RequestMapping("api/class")
public class ApiClassController {
    @Autowired
    private JTraceServer server;

    @RequestMapping("list")
    public Result list(@RequestParam String clientId, @RequestBody ClassCommand command) {
        ClassResponse classResponse = CommandUtil.dealCommand(server, clientId, command);
        if(classResponse.getStatus()== BaseResponse.STATUS_FAIL){
            return Result.fail(classResponse.getMsg());
        }
        return Result.ok(classResponse);
    }

    @RequestMapping("jad")
    public Result jad(@RequestParam String clientId
            , @RequestBody JadCommand command) {
        JadResponse jadResponse = CommandUtil.dealCommand(server, clientId, command);
        if(jadResponse.getStatus()== BaseResponse.STATUS_FAIL){
            return Result.fail(jadResponse.getMsg());
        }
        return Result.ok(jadResponse);
    }

    @RequestMapping("method")
    public Result method(@RequestParam String clientId, @RequestBody MethodCommand command) {
        MethodResponse response = CommandUtil.dealCommand(server, clientId, command);
        if(response.getStatus()== BaseResponse.STATUS_FAIL){
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }
}
