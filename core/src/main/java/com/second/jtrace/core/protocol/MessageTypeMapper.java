package com.second.jtrace.core.protocol;

import com.second.jtrace.core.command.jvm.HeapDumpCommand;
import com.second.jtrace.core.command.jvm.response.HeapDumpResponse;
import com.second.jtrace.core.response.BaseResponse;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeMapper {
    private static final Map<Integer,Class<? extends IMessage>> typeMapper = new HashMap<>();

    //emnu 枚举各种response
    public enum TypeList{
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
        VmOptionCommand,
        VmOptionResponse,
        ClientInfoCommand,
        ClientInfoResponse,


        PingMessage,
        PongMessage,

    }

    static {
        typeMapper.put(TypeList.BaseResponse.ordinal(), BaseResponse.class);
        typeMapper.put(TypeList.HeapDumpCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.HeapDumpResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.JVMCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.JVMResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.MemoryCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.MemoryResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.SysEnvCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.SysEnvResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.SysPropCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.SysPropResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.VmOptionCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.VmOptionResponse.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.PingMessage.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.PongMessage.ordinal(), HeapDumpResponse.class);
        typeMapper.put(TypeList.ClientInfoCommand.ordinal(), HeapDumpCommand.class);
        typeMapper.put(TypeList.ClientInfoResponse.ordinal(), HeapDumpResponse.class);


    }

    public  static Class<? extends IMessage> getClass(int type){
        return typeMapper.get(type);
    }
}
