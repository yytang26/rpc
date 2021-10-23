package com.tyy.rpc.io.protocol.codec;

import com.tyy.rpc.io.protocol.ProtocolConstant;
import com.tyy.rpc.io.protocol.ProtocolMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ProtocolEncoder extends MessageToByteEncoder<ProtocolMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtocolMsg protocolMsg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(ProtocolConstant.MAGIC);
        byteBuf.writeByte(ProtocolConstant.DEFAULT_VERSION);
        byteBuf.writeByte(protocolMsg.getMsgType());
        byteBuf.writeInt(protocolMsg.getContent().length);
        byteBuf.writeBytes(protocolMsg.getContent());
    }
}
