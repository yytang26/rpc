package com.tyy.rpc.registry.cache;

import com.google.common.collect.Maps;
import com.tyy.rpc.model.ServiceMetaData;
import java.util.Map;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ServerServiceDiscoveryCache {

    private static Map<String , ServiceMetaData> SERVICE_CACHE = Maps.newConcurrentMap();

    public static void put(String serviceId,ServiceMetaData serviceMetaData){
        SERVICE_CACHE.put(serviceId,serviceMetaData);
    }

    public static ServiceMetaData get(String serviceId){
        return SERVICE_CACHE.get(serviceId);
    }
}
