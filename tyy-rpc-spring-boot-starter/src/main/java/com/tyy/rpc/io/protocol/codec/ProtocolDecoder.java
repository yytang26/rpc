package com.tyy.rpc.io.protocol.codec;

import com.tyy.rpc.io.protocol.ProtocolConstant;
import com.tyy.rpc.io.protocol.ProtocolMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ProtocolDecoder extends ByteToMessageDecoder {

    public static final int BASE_LENGTH = 7;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < BASE_LENGTH) {
            return;
        }

        int beginIndex;
        while (true) {
            beginIndex = byteBuf.readerIndex();
            byteBuf.markReaderIndex();
            if (byteBuf.readByte() == ProtocolConstant.MAGIC) {
                break;
            }

            byteBuf.resetReaderIndex();
            byteBuf.readByte();

            if (byteBuf.readableBytes() < BASE_LENGTH) {
                return;
            }
        }
        byte version = byteBuf.readByte();
        byte type = byteBuf.readByte();
        int length = byteBuf.readInt();
        if (byteBuf.readableBytes() < length) {
            byteBuf.readerIndex(beginIndex);
            return;
        }
        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        ProtocolMsg msg = new ProtocolMsg();
        msg.setMsgType(type);
        msg.setContent(data);
        list.add(msg);

    }
}
