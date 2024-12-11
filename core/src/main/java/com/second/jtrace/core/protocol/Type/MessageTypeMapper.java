package com.second.jtrace.core.protocol.Type;

import com.second.jtrace.core.protocol.IMessage;
import com.second.jtrace.core.response.IResponse;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeMapper {
    private static final Map<Integer,Class<? extends IMessage>> typeMapper = new HashMap<>();

    //emnu 枚举各种response
    public enum TypeList{
        IRESPONSE,



        PingMessage,
        PongMessage,

    }

    static {
        typeMapper.put(TypeList.IRESPONSE.ordinal(), IResponse.class);
    }

    public  static Class<? extends IMessage> getClass(int type){
        return typeMapper.get(type);
    }
}
