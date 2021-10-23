package com.tyy.rpc.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.remote.request.ClientDetectionRequest;
import com.sun.org.apache.regexp.internal.RE;
import com.tyy.rpc.exception.RpcException;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.Registry;
import com.tyy.rpc.registry.ServiceURL;
import com.tyy.rpc.registry.cache.ClientServiceDiscoveryCache;
import com.tyy.rpc.registry.cache.ServerServiceDiscoveryCache;
import com.tyy.rpc.util.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tyy.rpc.common.constants.RpcConstant.SERVICE_PATH_DELIMITER;
import static com.tyy.rpc.common.constants.RpcConstant.UTF_8;

/**
 * @author:tyy
 * @date:2021/7/11
 */

@Slf4j
public class ZookeeperRegistry implements Registry {

    protected Integer servicePort;

    protected String serializer;

    protected Integer weight;

    protected ZkClient zkClient;

    public ZookeeperRegistry(Integer servicePort, String serializer, Integer weight, String zkAdress) {

        this.zkClient = new ZkClient(zkAdress);
        zkClient.setZkSerializer(new ZkSerializer() {
            @Override
            public byte[] serialize(Object o) throws ZkMarshallingError {
                return String.valueOf(o).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        });
        this.servicePort = servicePort;
        this.serializer = serializer;
        this.weight = weight;
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

        createZookeeperServiceNode(serviceURL);
    }

    private void createZookeeperServiceNode(ServiceURL serviceURL) {
        String urlJson = JSON.toJSONString(serviceURL);
        try {
            urlJson = URLEncoder.encode(urlJson, UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingExcepton utf8 encode", e);
        }

        String servicePath = ServiceUtils.getRegisterServiceParentPath(serviceURL.getServiceId());

        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
        }
        String urlPath = servicePath + SERVICE_PATH_DELIMITER + urlJson;
        if (zkClient.exists(urlPath)) {
            zkClient.delete(urlPath);
        }
        zkClient.createEphemeral(urlPath);
    }

    @Override
    public void subscribe(ServiceMetaData serviceMetaData) {
        getServiceList(serviceMetaData);
    }

    @Override
    public void subscribeServiceChange(ServiceMetaData serviceMetaData) {
        String serviceId = ServiceUtils.getServiceId(serviceMetaData);
        zkClient.subscribeChildChanges(ServiceUtils.getRegisterServiceParentPath(serviceId),
                new IZkChildListener() {
                    @Override
                    public void handleChildChange(String s, List<String> list) throws Exception {
                        log.debug("parentPath:{},currenntChilds:{}", s, list);

                        String[] arr = s.split("/");
                        String serviceId = arr[2];

                        ZookeeperRegistry.this.serviceChangeCallBack(serviceId, list);
                    }
                });
    }

    private void serviceChangeCallBack(String serviceId, List<String> list) {
        String lockKey = ClientServiceDiscoveryCache.getCacheLockKey(serviceId);
        synchronized (lockKey) {
            getAndSetServiceCache(serviceId, list);
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
                    String servicePath = ServiceUtils.getRegisterServiceParentPath(serviceId);
                    List<String> children = zkClient.getChildren(servicePath);
                    if (CollectionUtils.isEmpty(children)) {
                        throw new RpcException("No rpc provider" + serviceId + " available!");
                    }
                    services = getAndSetServiceCache(serviceId, children);
                }
            }
        }
        return services;
    }

    private List<ServiceURL> getAndSetServiceCache(String serviceId, List<String> children) {
        List<ServiceURL> services = Optional.ofNullable(children).orElse(Lists.newArrayList()).stream().map(serviceStr -> {
            String deCh = null;
            try {
                deCh = URLDecoder.decode(serviceStr, UTF_8);
            } catch (UnsupportedEncodingException e) {
                log.error("find service from zookeeper error", e);
            }
            return JSON.parseObject(deCh, ServiceURL.class);
        }).collect(Collectors.toList());

        ClientServiceDiscoveryCache.put(serviceId, services);
        return services;
    }
}
