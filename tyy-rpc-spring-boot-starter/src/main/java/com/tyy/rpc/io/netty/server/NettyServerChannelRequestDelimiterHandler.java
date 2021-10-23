package com.tyy.rpc.io.netty.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.domain.RpcStatusEnum;
import com.tyy.rpc.io.serializer.Serializer;
import com.tyy.rpc.io.server.ServerServiceInvocation;
import com.tyy.rpc.util.DelimiterUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Slf4j
public class NettyServerChannelRequestDelimiterHandler extends ChannelInboundHandlerAdapter {

    private static final ExecutorService executor = new ThreadPoolExecutor(4, 8,
            100, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            new ThreadFactoryBuilder().setNameFormat("rpcServer-%d").build());

    private ServerServiceInvocation serverServiceInvocation;

    private Serializer serializer;

    public NettyServerChannelRequestDelimiterHandler(ServerServiceInvocation serverServiceInvocation, Serializer serializer) {
        this.serverServiceInvocation = serverServiceInvocation;
        this.serializer = serializer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("netty channel active :{}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executor.submit(() -> {
            try {
                ByteBuf byteBuf = (ByteBuf) msg;

                byte[] reqData = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(reqData);

                ReferenceCountUtil.release(byteBuf);

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
                byte[] respData2 = DelimiterUtils.encodeDelimiterData(resData);
                ByteBuf buf = Unpooled.buffer(respData2.length);
                buf.writeBytes(respData2);
                ctx.writeAndFlush(buf);
            } catch (Exception e) {
                log.error("server process request error", e);
            }
        });
    }
}
