package com.second.jtrace.server.controller;


import com.second.jtrace.core.command.sampling.DisableSamplingCommand;
import com.second.jtrace.core.command.sampling.EnableSamplingCommand;
import com.second.jtrace.core.command.sampling.SamplingInfoCommand;
import com.second.jtrace.core.command.sampling.response.SamplingInfoResponse;
import com.second.jtrace.core.command.sampling.response.SamplingResponse;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.server.configuration.InfluxDBConfiguration;
import com.second.jtrace.server.dto.Result;
import com.second.jtrace.server.netty.JTraceServer;
import com.second.jtrace.server.util.CommandUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/sampling")
public class ApiSamplingController {
    @Autowired
    private JTraceServer server;

    @Autowired
    private InfluxDBConfiguration influxDBConfiguration;

    @RequestMapping("info")
    public Result detail(@RequestParam String clientId) {
        if (!influxDBConfiguration.isEnabled()) {
            return Result.fail("influxDB is not enable");
        }
        SamplingInfoResponse response = CommandUtil.dealCommand(server, clientId, new SamplingInfoCommand());
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("enable")
    public Result enable(@RequestParam String clientId, @RequestBody EnableSamplingCommand command) {
        if (!influxDBConfiguration.isEnabled()) {
            return Result.fail("influxDB is not enable");
        }
        SamplingResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

    @RequestMapping("disable")
    public Result disable(@RequestParam String clientId, @RequestBody DisableSamplingCommand command) {
        if (!influxDBConfiguration.isEnabled()) {
            return Result.fail("influxDB is not enable");
        }
        SamplingResponse response = CommandUtil.dealCommand(server, clientId, command);
        if (response.getStatus() == BaseResponse.STATUS_FAIL) {
            return Result.fail(response.getMsg());
        }
        return Result.ok(response);
    }

}
