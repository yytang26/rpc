package com.tyy.rpc.cluster;

import com.tyy.rpc.registry.ServiceURL;

import java.util.List;

/**
 * @author:tyy
 * @date:2021/7/10
 */
public interface LoadBalancer {

    String name();

    ServiceURL selectOne(List<ServiceURL> addresses);
}
