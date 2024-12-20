package com.second.jtrace.core.protocol;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.util.VersionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, IMessage> {
    private static final Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    @Override
    public void encode(ChannelHandlerContext ctx, IMessage msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeInt(JTraceConstants.MAGIC_NUMBER); // 4 bytes magic number
        out.writeByte(JTraceConstants.VERSION[0]); // 1 byte version
        out.writeByte(JTraceConstants.VERSION[1]); // 1 byte version
        out.writeByte(JTraceConstants.VERSION[2]); // 1 byte version
        out.writeByte(msg.getMessageTypeId()); // 1 byte class type


        // 6. 获取内容的字节数组
        byte[] bytes = GsonSerializer.serialize(msg);
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        if (magicNum != JTraceConstants.MAGIC_NUMBER) {
            logger.error("Received invalid magic number: " + magicNum + " from " + ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        byte[] version = new byte[3];
        for (int i = 0; i < 3; i++) {
            version[i] = in.readByte();
        }
        if (VersionUtil.compareVersion(version, JTraceConstants.VERSION) != 0) {
            logger.warn("Received invalid version: " + Arrays.toString(version) + " from " + ctx.channel().remoteAddress());
            return;
        }

        byte messageType = in.readByte();


        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        Class<? extends IMessage> messageClass = MessageTypeMapper.getClass(messageType);
        IMessage message = GsonSerializer.deserialize(messageClass, bytes);
        out.add(message);
    }

}
