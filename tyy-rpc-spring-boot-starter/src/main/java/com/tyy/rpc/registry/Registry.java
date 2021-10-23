package com.tyy.rpc.registry;

import com.tyy.rpc.model.ServiceMetaData;

import java.util.List;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface Registry {

    void registry(ServiceMetaData serviceMetaData) throws Exception;

    void subscribe(ServiceMetaData serviceMetaData);

    void subscribeServiceChange(ServiceMetaData serviceMetaData);

    List<ServiceURL> getServiceList(ServiceMetaData serviceMetaData);
}
