package com.second.jtrace.core.protocol;

public class PongMessage implements IMessage{
    @Override
    public int getMessageTypeId() {
        return 0;
    }
}
