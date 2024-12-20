package com.second.jtrace.core.protocol;

public interface IMessage {
    public default int getMessageTypeId() {
        return MessageTypeMapper.TypeList.getOrdinalByClassName(this);
    }
}
