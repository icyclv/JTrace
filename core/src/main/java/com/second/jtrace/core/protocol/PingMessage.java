package com.second.jtrace.core.protocol;


import com.second.jtrace.core.protocol.Type.MessageTypeMapper;

public class PingMessage implements IMessage {


    @Override
    public int getMessageTypeId() {
        return MessageTypeMapper.TypeList.PingMessage.ordinal();
    }
}
