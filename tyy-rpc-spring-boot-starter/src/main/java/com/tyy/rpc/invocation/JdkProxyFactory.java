package com.tyy.rpc.invocation;

import com.tyy.rpc.domain.RpcRequest;
import com.tyy.rpc.domain.RpcResponse;
import com.tyy.rpc.domain.RpcStatusEnum;
import com.tyy.rpc.exception.RpcException;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.ServiceURL;
import com.tyy.rpc.util.ServiceUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class JdkProxyFactory implements ProxyFactory {


    @Override
    public Object getProxy(ServiceMetaData serviceMetaData) {
        return Proxy.newProxyInstance(serviceMetaData.getClazz().getClassLoader(), new Class[]{serviceMetaData.getClazz()},
                new ClientInvocationHandler(serviceMetaData));
    }

    private class ClientInvocationHandler implements InvocationHandler {
        private ServiceMetaData serviceMetaData;

        public ClientInvocationHandler(ServiceMetaData serviceMetaData) {
            this.serviceMetaData = serviceMetaData;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String serviceId = ServiceUtils.getServiceId(serviceMetaData);

            ServiceURL serviceURL = InvocationServiceSelector.select(serviceMetaData);

            RpcRequest request = new RpcRequest();
            request.setMethod(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceId(serviceId);

            RpcResponse response = InvocationClientContainer.getInvocationClient(
                    serviceURL.getServerNet()).invoke(request, serviceURL);

            if (response.getStatus() == RpcStatusEnum.SUCCESS) {
                return response.getData();
            } else if (response.getException() != null) {
                throw new RpcException(response.getException().getMessage());
            } else {
                throw new RpcException(response.getStatus().name());
            }
        }
    }
}
