package com.tyy.rpc.io.client;

import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.registry.ServiceURL;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface InvocationClient {

    RpcResponse invoke(RpcRequest request, ServiceURL serviceURL);
}
