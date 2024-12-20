package com.second.jtrace.core.protocol;

import com.second.jtrace.core.command.clazz.ClassCommand;
import com.second.jtrace.core.command.clazz.ClassLoaderCommand;
import com.second.jtrace.core.command.clazz.JadCommand;
import com.second.jtrace.core.command.clazz.MethodCommand;
import com.second.jtrace.core.command.clazz.response.ClassLoaderResponse;
import com.second.jtrace.core.command.clazz.response.ClassResponse;
import com.second.jtrace.core.command.clazz.response.JadResponse;
import com.second.jtrace.core.command.clazz.response.MethodResponse;
import com.second.jtrace.core.command.client.ClientInfoCommand;
import com.second.jtrace.core.command.client.response.ClientInfoResponse;
import com.second.jtrace.core.command.enhance.ResetCommand;
import com.second.jtrace.core.command.enhance.StackCommand;
import com.second.jtrace.core.command.enhance.TraceCommand;
import com.second.jtrace.core.command.enhance.WatchCommand;
import com.second.jtrace.core.command.enhance.response.*;
import com.second.jtrace.core.command.jvm.*;
import com.second.jtrace.core.command.jvm.response.*;
import com.second.jtrace.core.command.logger.LoggerInfoCommand;
import com.second.jtrace.core.command.logger.LoggerLevelCommand;
import com.second.jtrace.core.command.logger.response.LoggerInfoResponse;
import com.second.jtrace.core.command.logger.response.LoggerLevelResponse;
import com.second.jtrace.core.command.ognl.OgnlCommand;
import com.second.jtrace.core.command.ognl.response.OgnlResponse;
import com.second.jtrace.core.command.sampling.DisableSamplingCommand;
import com.second.jtrace.core.command.sampling.EnableSamplingCommand;
import com.second.jtrace.core.command.sampling.SamplingInfoCommand;
import com.second.jtrace.core.command.sampling.response.SamplingInfoResponse;
import com.second.jtrace.core.command.sampling.response.SamplingResponse;
import com.second.jtrace.core.command.shutdown.ShutdownMessage;
import com.second.jtrace.core.command.thread.ThreadAllCommand;
import com.second.jtrace.core.command.thread.response.ThreadAllResponse;
import com.second.jtrace.core.command.vmtool.VMToolCommand;
import com.second.jtrace.core.command.vmtool.response.VMToolResponse;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.core.sampling.bean.SamplingMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeMapper {
    private static final Map<Integer, Class<? extends IMessage>> typeMapper = new HashMap<>();

    static {
        typeMapper.put(TypeList.BaseResponse.ordinal(), BaseResponse.class);
        typeMapper.put(TypeList.HeapDumpCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.HeapDumpResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.JVMCommand.ordinal(), JVMCommand.class);
        typeMapper.put(TypeList.JVMResponse.ordinal(), JVMResponse.class);
        typeMapper.put(TypeList.MemoryCommand.ordinal(), MemoryCommand.class);

        typeMapper.put(TypeList.MemoryResponse.ordinal(), MemoryResponse.class);
        typeMapper.put(TypeList.SysEnvCommand.ordinal(), SysEnvCommand.class);
        typeMapper.put(TypeList.SysEnvResponse.ordinal(), SysEnvResponse.class);
        typeMapper.put(TypeList.SysPropCommand.ordinal(), SysPropCommand.class);
        typeMapper.put(TypeList.SysPropResponse.ordinal(), SysPropResponse.class);
        typeMapper.put(TypeList.VMOptionCommand.ordinal(), VMOptionCommand.class);
        typeMapper.put(TypeList.VMOptionResponse.ordinal(), VMOptionResponse.class);
        typeMapper.put(TypeList.PingMessage.ordinal(), PingMessage.class);
        typeMapper.put(TypeList.ClientInfoCommand.ordinal(), ClientInfoCommand.class);
        typeMapper.put(TypeList.ClientInfoResponse.ordinal(), ClientInfoResponse.class);

        typeMapper.put(TypeList.ClassCommand.ordinal(), ClassCommand.class);
        typeMapper.put(TypeList.ClassResponse.ordinal(), ClassResponse.class);
        typeMapper.put(TypeList.ClassLoaderCommand.ordinal(), ClassLoaderCommand.class);
        typeMapper.put(TypeList.ClassLoaderResponse.ordinal(), ClassLoaderResponse.class);
        typeMapper.put(TypeList.JadCommand.ordinal(), JadCommand.class);
        typeMapper.put(TypeList.JadResponse.ordinal(), JadResponse.class);
        typeMapper.put(TypeList.MethodCommand.ordinal(), MethodCommand.class);
        typeMapper.put(TypeList.MethodResponse.ordinal(), MethodResponse.class);
        typeMapper.put(TypeList.ResetCommand.ordinal(), ResetCommand.class);
        typeMapper.put(TypeList.ResetResponse.ordinal(), ResetResponse.class);
        typeMapper.put(TypeList.StackCommand.ordinal(), StackCommand.class);
        typeMapper.put(TypeList.StackResponse.ordinal(), StackResponse.class);
        typeMapper.put(TypeList.TraceCommand.ordinal(), TraceCommand.class);
        typeMapper.put(TypeList.TraceResponse.ordinal(), TraceResponse.class);
        typeMapper.put(TypeList.WatchCommand.ordinal(), WatchCommand.class);
        typeMapper.put(TypeList.WatchResponse.ordinal(), WatchResponse.class);
        typeMapper.put(TypeList.BaseEnhanceResponse.ordinal(), BaseEnhanceResponse.class);
        typeMapper.put(TypeList.StackAsyncResponse.ordinal(), StackAsyncResponse.class);
        typeMapper.put(TypeList.TraceAsyncResponse.ordinal(), TraceAsyncResponse.class);
        typeMapper.put(TypeList.WatchAsyncResponse.ordinal(), WatchAsyncResponse.class);
        typeMapper.put(TypeList.LoggerInfoCommand.ordinal(), LoggerInfoCommand.class);
        typeMapper.put(TypeList.LoggerInfoResponse.ordinal(), LoggerInfoResponse.class);
        typeMapper.put(TypeList.LoggerLevelCommand.ordinal(), LoggerLevelCommand.class);
        typeMapper.put(TypeList.LoggerLevelResponse.ordinal(), LoggerLevelResponse.class);
        typeMapper.put(TypeList.OgnlCommand.ordinal(), OgnlCommand.class);
        typeMapper.put(TypeList.OgnlResponse.ordinal(), OgnlResponse.class);
        typeMapper.put(TypeList.ThreadAllCommand.ordinal(), ThreadAllCommand.class);
        typeMapper.put(TypeList.ThreadAllResponse.ordinal(), ThreadAllResponse.class);
        typeMapper.put(TypeList.VMToolCommand.ordinal(), VMToolCommand.class);
        typeMapper.put(TypeList.VMToolResponse.ordinal(), VMToolResponse.class);

        typeMapper.put(TypeList.ShutdownMessage.ordinal(), ShutdownMessage.class);
        typeMapper.put(TypeList.SamplingMessage.ordinal(), SamplingMessage.class);

        typeMapper.put(TypeList.DisableSamplingCommand.ordinal(), DisableSamplingCommand.class);
        typeMapper.put(TypeList.SamplingInfoCommand.ordinal(), SamplingInfoCommand.class);
        typeMapper.put(TypeList.EnableSamplingCommand.ordinal(), EnableSamplingCommand.class);
        typeMapper.put(TypeList.SamplingInfoResponse.ordinal(), SamplingInfoResponse.class);
        typeMapper.put(TypeList.SamplingResponse.ordinal(), SamplingResponse.class);


    }

    public static Class<? extends IMessage> getClass(int type) {
        return typeMapper.get(type);
    }

    public enum TypeList {
        BaseResponse,
        HeapDumpCommand,
        HeapDumpResponse,
        JVMCommand,
        JVMResponse,
        MemoryCommand,
        MemoryResponse,
        SysEnvCommand,
        SysEnvResponse,
        SysPropCommand,
        SysPropResponse,
        VMOptionCommand,
        VMOptionResponse,
        ClientInfoCommand,
        ClientInfoResponse,
        ClassCommand,
        ClassResponse,
        ClassLoaderCommand,
        ClassLoaderResponse,
        JadCommand,
        JadResponse,
        MethodCommand,
        MethodResponse,
        ResetCommand,
        ResetResponse,
        StackCommand,
        StackResponse,
        TraceCommand,
        TraceResponse,
        WatchCommand,
        WatchResponse,
        BaseEnhanceResponse,
        StackAsyncResponse,
        TraceAsyncResponse,
        WatchAsyncResponse,
        LoggerInfoCommand,
        LoggerInfoResponse,
        LoggerLevelCommand,
        LoggerLevelResponse,
        OgnlCommand,
        OgnlResponse,
        ThreadAllCommand,
        ThreadAllResponse,
        VMToolCommand,
        VMToolResponse,
        ShutdownMessage,
        SamplingMessage,
        DisableSamplingCommand,
        SamplingInfoCommand,
        EnableSamplingCommand,
        SamplingInfoResponse,
        SamplingResponse,
        PingMessage;


        public static int getOrdinalByClassName(Object obj) {
            try {
                String className = obj.getClass().getSimpleName();

                return TypeList.valueOf(className).ordinal();
            } catch (IllegalArgumentException e) {
                return -1; // 或者 throw new IllegalArgumentException("No matching enum for class: " + obj.getClass().getSimpleName());
            }
        }

    }
}
