package com.tyy.rpc.registry.nacos;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.tyy.rpc.exception.RpcException;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.Registry;
import com.tyy.rpc.registry.ServiceURL;
import com.tyy.rpc.registry.cache.ClientServiceDiscoveryCache;
import com.tyy.rpc.registry.cache.ServerServiceDiscoveryCache;
import com.tyy.rpc.util.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.tyy.rpc.common.constants.RpcConstant.UTF_8;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Slf4j
public class NacosRegistry implements Registry {

    protected Integer servicePort;

    protected String serializer;

    protected Integer weight;

    protected NamingService namingService;

    public NacosRegistry(Integer servicePort, String serializer, Integer weight, String nacosAdress) throws NacosException{
        this.servicePort = servicePort;
        this.serializer = serializer;
        this.weight = weight;
        this.namingService = NamingFactory.createNamingService(nacosAdress);
    }

    @Override
    public void registry(ServiceMetaData serviceMetaData) throws Exception {
        String serviceId = ServiceUtils.getServiceId(serviceMetaData);
        ServerServiceDiscoveryCache.put(serviceId, serviceMetaData);

        ServiceURL serviceURL = new ServiceURL();
        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + this.servicePort;
        serviceURL.setAddress(address);
        serviceURL.setServiceId(serviceId);
        serviceURL.setName(serviceMetaData.getName());
        serviceURL.setVersion(serviceMetaData.getVersion());
        serviceURL.setSerializer(this.serializer);
        serviceURL.setWeight(this.weight);

        String urlJson = JSON.toJSONString(serviceURL);
        try {
            urlJson = URLEncoder.encode(urlJson, UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException utf8 encode", e);
        }

        Instance instance = new Instance();
        instance.setIp(host);
        instance.setPort(this.servicePort);
        instance.setHealthy(false);
        instance.setWeight(2.0);

        Map<String, String> instanceMeta = new HashMap<>();
        instanceMeta.put("serviceURL", urlJson);
        instance.setMetadata(instanceMeta);

        namingService.registerInstance(serviceId, instance);
    }

    @Override
    public void subscribe(ServiceMetaData serviceMetaData) {
        getServiceList(serviceMetaData);
    }

    @Override
    public void subscribeServiceChange(ServiceMetaData serviceMetaData) {
        String serviceId = ServiceUtils.getServiceId(serviceMetaData);

        try {
            namingService.subscribe(serviceId, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    if (event instanceof NamingEvent) {
                        List<Instance> instances = ((NamingEvent) event).getInstances();
                        String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
                        synchronized (lockKey) {
                            getAndSetServiceCache(serviceId, instances);
                        }
                    }
                }
            });
        } catch (NacosException e) {
            log.error("subscribeServiceChange from nacos error", e);
            throw new RpcException("subscribe service change from nacos error,serviceId = " + serviceId);
        }
    }

    @Override
    public List<ServiceURL> getServiceList(ServiceMetaData serviceMetaData) {
        String serviceId = ServiceUtils.getServiceId(serviceMetaData);
        List<ServiceURL> services = ClientServiceDiscoveryCache.get(serviceId);

        if (CollectionUtils.isEmpty(services)) {
            String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
            synchronized (lockKey) {
                services = ClientServiceDiscoveryCache.get(serviceId);
                if (CollectionUtils.isEmpty(services)) {
                    try {
                        List<Instance> instances = namingService.getAllInstances(serviceId);
                        if (CollectionUtils.isEmpty(instances)) {
                            throw new RpcException("No rpc provider" + serviceId + "available!");
                        }
                        services = getAndSetServiceCache(serviceId, instances);
                    } catch (Exception e) {
                        log.error("getServiceList from nacos error", e);
                        throw new RpcException("get service list from nacos error,serviceId=" + serviceId);
                    }
                }
            }
        }
        return services;
    }

    public List<ServiceURL> getAndSetServiceCache(String serviceId, List<Instance> instances) {

        List<ServiceURL> serviceURLS = Optional.ofNullable(instances).orElse(Lists.newArrayList()).stream().map(instance -> {
            String deCh = null;
            try {
                Map<String, String> metaData = instance.getMetadata();
                String serviceStr = metaData.get("serviceURL");
                deCh = URLDecoder.decode(serviceStr, UTF_8);
            } catch (UnsupportedEncodingException e) {

            }
            return JSON.parseObject(deCh, ServiceURL.class);
        }).collect(Collectors.toList());
        ClientServiceDiscoveryCache.put(serviceId, serviceURLS);
        return serviceURLS;
    }
}
