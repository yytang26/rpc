package com.tyy.rpc.invocation;

import com.tyy.rpc.cluster.LoadBalancer;
import com.tyy.rpc.exception.RpcException;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.Registry;
import com.tyy.rpc.registry.ServiceURL;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class InvocationServiceSelector {

    private static LoadBalancer loadBalancer;

    private static Registry registry;

    public static ServiceURL select(ServiceMetaData serviceMetaData) {
        List<ServiceURL> serviceList = registry.getServiceList(serviceMetaData);
        if (CollectionUtils.isEmpty(serviceList)) {
            throw new RpcException("No rpc provider:" + serviceMetaData.getName() + "version:" + serviceMetaData.getVersion() + "available");
        }
        ServiceURL serviceURL = loadBalancer.selectOne(serviceList);
        return serviceURL;
    }

    public static void setLoadBalancer(LoadBalancer loadBalancer) {
        InvocationServiceSelector.loadBalancer = loadBalancer;
    }

    public static void setRegistry(Registry registry) {
        InvocationServiceSelector.registry = registry;
    }
}
