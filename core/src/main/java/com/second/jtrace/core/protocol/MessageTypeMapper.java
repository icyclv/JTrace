package com.second.jtrace.core.protocol;

import com.second.jtrace.core.command.client.ClientInfoCommand;
import com.second.jtrace.core.command.client.response.ClientInfoResponse;
import com.second.jtrace.core.command.jvm.*;
import com.second.jtrace.core.command.jvm.response.*;
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
        VMOptionCommand,
        VMOptionResponse,
        ClientInfoCommand,
        ClientInfoResponse,


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


    }

    public  static Class<? extends IMessage> getClass(int type){
        return typeMapper.get(type);
    }
}
