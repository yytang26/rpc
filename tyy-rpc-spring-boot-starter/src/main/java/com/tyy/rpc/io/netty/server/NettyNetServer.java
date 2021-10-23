package com.tyy.rpc.io.netty.server;

import com.tyy.rpc.io.protocol.codec.ProtocolDecoder;
import com.tyy.rpc.io.protocol.codec.ProtocolEncoder;
import com.tyy.rpc.io.server.DefaultServerServiceInvocation;
import com.tyy.rpc.io.server.NetServer;
import com.tyy.rpc.io.server.ServerServiceInvocation;
import com.tyy.rpc.util.SpiLoaderUtils;
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;



/**
 * @author:tyy
 * @date:2021/7/11
 */
public class NettyNetServer implements NetServer {

    private Channel channel;

    private int serverPort;

    private String serializer;

    private ServerServiceInvocation requestProcessor;

    public NettyNetServer(int serverPort, String serializer) {
        this.serverPort = serverPort;
        this.serializer = serializer;
        init();
        ;
    }

    private void init() {
        this.requestProcessor = new DefaultServerServiceInvocation();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workderGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workderGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ProtocolEncoder());
                            pipeline.addLast(new ProtocolDecoder());
                            pipeline.addLast(new NettyServerChannelRequestHandler(requestProcessor, SpiLoaderUtils.getSerializer(serializer)));
                        }
                    });

            ChannelFuture future = b.bind(serverPort).sync();
            channel = future.channel();

            future.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            bossGroup.shutdownGracefully();
            workderGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        this.channel.close();
    }
}
