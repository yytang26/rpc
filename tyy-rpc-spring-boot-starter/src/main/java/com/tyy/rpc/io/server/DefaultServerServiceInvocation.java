package com.tyy.rpc.io.server;

import com.tyy.rpc.context.BeanContext;
import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.domain.RpcStatusEnum;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.cache.ServerServiceDiscoveryCache;

import java.lang.reflect.Method;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class DefaultServerServiceInvocation implements ServerServiceInvocation {

    public DefaultServerServiceInvocation() {
    }

    @Override
    public RpcResponse handleRequest(RpcRequest rpcRequest) throws Exception {
        ServiceMetaData serviceMetaData = ServerServiceDiscoveryCache.get(rpcRequest.getServiceId());

        RpcResponse response = null;

        if (null == serviceMetaData) {
            response = new RpcResponse(RpcStatusEnum.NOT_FOUND);
        } else {
            try {
                Method method = serviceMetaData.getClazz().getMethod(rpcRequest.getMethod(), rpcRequest.getParameterTypes());
                Object returnValue = method.invoke(BeanContext.getBean(serviceMetaData.getClazz()), rpcRequest.getParameters());
                response = new RpcResponse(RpcStatusEnum.SUCCESS);
                response.setData(returnValue);
            } catch (Exception e) {
                response = new RpcResponse(RpcStatusEnum.ERROR);

            }
        }
        response.setRequestId(rpcRequest.getRequestId());
        return response;
    }
}
