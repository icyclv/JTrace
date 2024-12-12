package com.second.jtrace.core.protocol;


public class PingMessage implements IMessage {


    @Override
    public int getMessageTypeId() {
        return MessageTypeMapper.TypeList.PingMessage.ordinal();
    }
}
