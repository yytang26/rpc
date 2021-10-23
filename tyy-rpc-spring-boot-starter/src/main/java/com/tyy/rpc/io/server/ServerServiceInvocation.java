package com.tyy.rpc.io.server;

import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface ServerServiceInvocation {

    RpcResponse handleRequest(RpcRequest rpcRequest) throws Exception;
}
