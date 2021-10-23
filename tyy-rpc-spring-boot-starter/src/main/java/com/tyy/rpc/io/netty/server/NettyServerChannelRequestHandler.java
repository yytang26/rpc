package com.tyy.rpc.io.netty.server;

import com.alibaba.nacos.shaded.com.google.protobuf.ProtocolMessageEnum;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.domain.RpcStatusEnum;
import com.tyy.rpc.io.protocol.ProtocolConstant;
import com.tyy.rpc.io.protocol.ProtocolMsg;
import com.tyy.rpc.io.serializer.Serializer;
import com.tyy.rpc.io.server.ServerServiceInvocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class NettyServerChannelRequestHandler extends ChannelInboundHandlerAdapter {
    private static final ExecutorService executor = new ThreadPoolExecutor(4, 8, 100, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10000), new ThreadFactoryBuilder().setNameFormat("rpcServer-%d").build());

    private ServerServiceInvocation serverServiceInvocation;

    private Serializer serializer;

    public NettyServerChannelRequestHandler(ServerServiceInvocation serverServiceInvocation, Serializer serializer) {
        this.serializer = serializer;
        this.serverServiceInvocation = serverServiceInvocation;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.submit(() -> {
            try {
                ProtocolMsg reqMsg = (ProtocolMsg) msg;
                byte[] reqData = reqMsg.getContent();
                RpcRequest request = this.serializer.deserialize(reqData, RpcRequest.class);
                RpcResponse response = serverServiceInvocation.handleRequest(request);
                byte[] resData = null;
                try {
                    resData = this.serializer.serialize(response);
                } catch (Exception e) {
                    RpcResponse errRes = new RpcResponse(RpcStatusEnum.ERROR);
                    errRes.setRequestId(request.getRequestId());
                    errRes.setException(e);
                    resData = this.serializer.serialize(errRes);
                }

                ProtocolMsg resMsg = new ProtocolMsg();
                resMsg.setMsgType(ProtocolConstant.RES_TYPE);
                resMsg.setContent(resData);

                ctx.writeAndFlush(resMsg);
            } catch (Exception e) {

            }
        });
    }
}
