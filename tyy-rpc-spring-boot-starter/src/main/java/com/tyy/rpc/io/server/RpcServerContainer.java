package com.tyy.rpc.io.server;


import com.tyy.rpc.config.RpcProperties;
import com.tyy.rpc.io.netty.server.NettyNetServer;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class RpcServerContainer {

    private static NetServer netServer;

    public static NetServer initServer(RpcProperties properties){
        if(netServer==null){
            netServer = new NettyNetServer(properties.getServerPort(),properties.getSerializer());
        }
        return netServer;
    }
}
