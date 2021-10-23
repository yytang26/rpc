package com.tyy.rpc.io.client;

import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface ClientRequestHandler {

    RpcResponse send(RpcRequest rpcRequest);
}
