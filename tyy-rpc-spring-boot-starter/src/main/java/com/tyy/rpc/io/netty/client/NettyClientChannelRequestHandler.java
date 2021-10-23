package com.tyy.rpc.io.netty.client;

import com.tyy.rpc.config.RpcProperties;
import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.exception.RpcException;
import com.tyy.rpc.io.client.ClientRequestHandler;
import com.tyy.rpc.io.client.ClientServiceAddressHandlerCache;
import com.tyy.rpc.io.protocol.ProtocolConstant;
import com.tyy.rpc.io.protocol.ProtocolMsg;
import com.tyy.rpc.io.serializer.Serializer;
import com.tyy.rpc.util.DelimiterUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Slf4j
public class NettyClientChannelRequestHandler extends ChannelInboundHandlerAdapter implements ClientRequestHandler {

    static final int CHANNEL_WAIT_TIME = 8;

    static final int RESPONSE_WAIT_TIME = 8;

    private volatile Channel channel;

    private volatile ChannelHandlerContext ctx;

    private String remoteAddr;

    private static Map<String,InvokeFuture<RpcResponse>> requestMap = new ConcurrentHashMap<>();

    private Serializer serializer;

    private CountDownLatch latch = new CountDownLatch(1);

    public NettyClientChannelRequestHandler(Serializer serializer,String remoteAddr) {
        this.serializer = serializer;
        this.remoteAddr = remoteAddr;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        this.channel = ctx.channel();
        this.ctx = ctx;
        latch.countDown();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolMsg resMsg = (ProtocolMsg) msg;
        RpcResponse response = serializer.deserialize(resMsg.getContent(),RpcResponse.class);
        InvokeFuture<RpcResponse> future = requestMap.get(response.getRequestId());
        future.setResponse(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ClientServiceAddressHandlerCache.remove(remoteAddr);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ClientServiceAddressHandlerCache.remove(remoteAddr);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public RpcResponse send(RpcRequest rpcRequest) {
        RpcResponse response;
        InvokeFuture<RpcResponse> future = new InvokeFuture<>();
        requestMap.put(rpcRequest.getRequestId(),future);
        try {
            byte[] data = serializer.serialize(rpcRequest);

            if(latch.await(CHANNEL_WAIT_TIME, TimeUnit.SECONDS)) {
               ProtocolMsg msg = new ProtocolMsg();
               msg.setContent(data);
               msg.setMsgType(ProtocolConstant.REQ_TYPE);
               ctx.writeAndFlush(msg);

                response = future.get(RESPONSE_WAIT_TIME,TimeUnit.SECONDS);

                if(response == null) {
                    throw new RpcException(rpcRequest.getServiceId()+" request time out");
                }
            } else {
                ClientServiceAddressHandlerCache.remove(remoteAddr);
                try {
                    ctx.close();
                } catch (Exception e) {
                    log.error("close channel exception",e);
                }
                throw new RpcException("establish channel time out");
            }
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        } finally {
            requestMap.remove(rpcRequest.getRequestId());
        }
        return response;
    }

}
