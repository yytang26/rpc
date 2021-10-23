package com.tyy.rpc.invocation;

import com.tyy.rpc.annotation.RpcConsumer;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.cache.ClientServiceDiscoveryCache;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class RpcConsumerBeanDefinitionBuilder {

    private final Class<?> interfaceClass;

    private final RpcConsumer rpcConsumer;

    public RpcConsumerBeanDefinitionBuilder(Class<?> interfaceClass,RpcConsumer rpcConsumer) {
        this.interfaceClass = interfaceClass;
        this.rpcConsumer = rpcConsumer;
    }

    public BeanDefinition build() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RpcConsumer.class);

        ServiceMetaData serviceMetaData = new ServiceMetaData();
        serviceMetaData.setClazz(interfaceClass);
        serviceMetaData.setName(interfaceClass.getName());
        serviceMetaData.setVersion(rpcConsumer.version());

        beanDefinitionBuilder.addPropertyValue("serviceMetadata",serviceMetaData);

        ClientServiceDiscoveryCache.addServiceMetadata(serviceMetaData);

        return beanDefinitionBuilder.getBeanDefinition();
    }
}
