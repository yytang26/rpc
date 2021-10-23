package com.tyy.rpc.io.client;

import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.invocation.InvocationClientContainer;
import com.tyy.rpc.io.netty.client.NettyNetClient;
import com.tyy.rpc.io.serializer.Serializer;
import com.tyy.rpc.registry.ServiceURL;
import com.tyy.rpc.util.SpiLoaderUtils;
import lombok.extern.slf4j.Slf4j;

import javax.naming.ldap.PagedResultsControl;
import java.util.Map;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Slf4j
public class DefaultInvocationClient implements InvocationClient {

    private static DefaultInvocationClient INSTANCE_IP;

    static {
        INSTANCE_IP = new DefaultInvocationClient(new NettyNetClient());
    }

    private NetClient rpcClient;

    private Map<String, Serializer> supportSerializerMap;

    private DefaultInvocationClient() {

    }

    private DefaultInvocationClient(NetClient netClient) {
        this.rpcClient = netClient;
        init();
    }

    private void init() {
        supportSerializerMap = SpiLoaderUtils.getSupportSerializer();
    }

    public static DefaultInvocationClient getInstance(String serverNet) {
        return INSTANCE_IP;
    }


    public RpcResponse invoke(RpcRequest rpcRequest, ServiceURL serviceURL) {
        String address = serviceURL.getAddress();

        ClientRequestHandler handler = ClientServiceAddressHandlerCache.get(address);
        if (handler == null) {
            Serializer serializer = supportSerializerMap.get(serviceURL.getSerializer());
            handler = rpcClient.connect(address, serializer);
            log.debug("establish new channel");
        }
        return handler.send(rpcRequest);
    }

}

