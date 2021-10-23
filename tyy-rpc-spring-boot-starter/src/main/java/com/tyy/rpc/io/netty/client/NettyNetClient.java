package com.tyy.rpc.io.netty.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tyy.rpc.io.client.ClientRequestHandler;
import com.tyy.rpc.io.client.ClientServiceAddressHandlerCache;
import com.tyy.rpc.io.client.NetClient;
import com.tyy.rpc.io.protocol.codec.ProtocolDecoder;
import com.tyy.rpc.io.protocol.codec.ProtocolEncoder;
import com.tyy.rpc.io.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class NettyNetClient implements NetClient {

    private static ExecutorService threadPool = new ThreadPoolExecutor(4, 10, 200,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000),
            new ThreadFactoryBuilder().setNameFormat("rpcClient-%d")
                    .build());

    private EventLoopGroup clientGroup = new NioEventLoopGroup(4);

    @Override
    public ClientRequestHandler connect(String address, Serializer serializer) {
        String[] adderInfo = address.split(":");
        String serveIp = adderInfo[0];
        String serverPort = adderInfo[1];
        NettyClientChannelRequestHandler handler = new NettyClientChannelRequestHandler(serializer, address);
        threadPool.submit(() -> {
            Bootstrap b = new Bootstrap();
            b.group(clientGroup).channel(NioSctpChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ProtocolEncoder());
                            pipeline.addLast(new ProtocolDecoder());

                            pipeline.addLast(handler);
                        }
                    });
            ChannelFuture channelFuture = b.connect(serveIp, Integer.parseInt(serverPort));
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    ClientServiceAddressHandlerCache.put(address, handler);
                }
            });
        });
        return handler;
    }
}
