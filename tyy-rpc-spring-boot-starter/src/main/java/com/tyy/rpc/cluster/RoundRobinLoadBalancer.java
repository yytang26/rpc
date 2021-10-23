package com.tyy.rpc.cluster;

import com.tyy.rpc.registry.ServiceURL;
import com.tyy.rpc.util.AtomicPositiveInteger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final static Map<String, AtomicPositiveInteger> sequences = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return null;
    }

    @Override
    public ServiceURL selectOne(List<ServiceURL> addresses) {
        return null;
    }
}
