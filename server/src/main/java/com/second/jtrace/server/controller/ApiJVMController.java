package com.second.jtrace.server.controller;


import com.second.jtrace.core.command.clazz.ClassLoaderCommand;
import com.second.jtrace.core.command.clazz.response.ClassLoaderResponse;
import com.second.jtrace.core.command.jvm.*;
import com.second.jtrace.core.command.jvm.response.*;
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
@RequestMapping("api/jvm")
public class ApiJVMController {
    @Autowired
    private JTraceServer server;

    @RequestMapping("classloader")
    public Result classloader(@RequestParam String clientId) {
        ClassLoaderCommand command = new ClassLoaderCommand();
        ClassLoaderResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("info")
    public Result info(@RequestParam String clientId) {
        JVMCommand command = new JVMCommand();
        JVMResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("memory")
    public Result memory(@RequestParam String clientId) {
        MemoryCommand command = new MemoryCommand();
        MemoryResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }


    @RequestMapping("sysprop")
    public Result sysprop(@RequestParam String clientId) {
        SysPropCommand command = new SysPropCommand();
        SysPropResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("sysenv")
    public Result sysenv(@RequestParam String clientId) {
        SysEnvCommand command = new SysEnvCommand();
        SysEnvResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("vmoption")
    public Result vmoption(@RequestParam String clientId) {
        VMOptionCommand command = new VMOptionCommand();
        VMOptionResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("heapdump")
    public Result heapdump(@RequestParam String clientId, @RequestBody HeapDumpCommand command) {
        HeapDumpResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }
}
