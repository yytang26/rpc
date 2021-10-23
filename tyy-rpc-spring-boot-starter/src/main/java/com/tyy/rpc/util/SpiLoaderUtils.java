package com.tyy.rpc.util;

import com.google.common.collect.Maps;
import com.tyy.rpc.cluster.LoadBalancer;
import com.tyy.rpc.exception.RpcException;
import com.tyy.rpc.io.serializer.Serializer;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class SpiLoaderUtils {

    public static Serializer getSerializer(String name) {
        Serializer serializer = getSupportSerializer().get(name);
        return Optional.ofNullable(serializer).orElseThrow(() -> new RpcException("serialize config" + name + "not exists!"));
    }

    public static Map<String, Serializer> getSupportSerializer() {
        Map<String, Serializer> map = Maps.newHashMap();
        ServiceLoader<Serializer> loader = ServiceLoader.load(Serializer.class);
        Iterator<Serializer> iterator = loader.iterator();
        while (iterator.hasNext()) {
            Serializer serializer = iterator.next();
            map.put(serializer.name(), serializer);
        }
        return map;
    }

    public static LoadBalancer getLoadBalancer(String loadBalance) {
        ServiceLoader<LoadBalancer> loadBalancers = ServiceLoader.load(LoadBalancer.class);
        Iterator<LoadBalancer> iterator = loadBalancers.iterator();
        while (iterator.hasNext()) {
            LoadBalancer loadBalancer = iterator.next();
            if (loadBalance.equals(loadBalancer.name())) {
                return loadBalancer;
            }
        }
        throw new RpcException("load balance config" + loadBalance + "not exist!");
    }
}
