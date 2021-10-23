package com.tyy.rpc.invocation;

import com.tyy.rpc.model.ServiceMetaData;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface ProxyFactory {

    Object getProxy(ServiceMetaData serviceMetaData);
}
