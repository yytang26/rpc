package com.tyy.rpc.registry.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.ServiceURL;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ClientServiceDiscoveryCache {

    private static Map<String, List<ServiceURL>> SERVICE_URL_CACHE = Maps.newConcurrentMap();

    private static List<ServiceMetaData> SERVICE_METADATA_LIST = Lists.newArrayList();

    private static Map<String,String> serviceIdLockMap = Maps.newConcurrentMap();

    public static void put(String serviceId,List<ServiceURL> serviceURLList){
        SERVICE_URL_CACHE.put(serviceId,serviceURLList);
    }

    public static void remove(String serviceId,ServiceURL serviceURL){
        SERVICE_URL_CACHE.computeIfPresent(serviceId,(key,value)->
                value.stream().filter(o->!o.equals(serviceURL)).collect(Collectors.toList()));
    }

    public static void removeAll(String serviceId){
        SERVICE_URL_CACHE.remove(serviceId);
    }

    public static boolean isNotExits(String serviceId){
        return SERVICE_URL_CACHE.get(serviceId) == null || SERVICE_URL_CACHE.get(serviceId).size()==0;
    }

    public static List<ServiceURL> get(String serviceId){
        return SERVICE_URL_CACHE.get(serviceId);
    }

    public static void addServiceMetadata(ServiceMetaData serviceMetaData){
        SERVICE_METADATA_LIST.add(serviceMetaData);
    }

    public static List<ServiceMetaData> getAllServiceMetadata(){
        return ImmutableList.copyOf(SERVICE_METADATA_LIST);
    }

    public static String getCacheLockKey(String serviceId){
        return serviceIdLockMap.computeIfAbsent(serviceId,key->key);
    }



}
