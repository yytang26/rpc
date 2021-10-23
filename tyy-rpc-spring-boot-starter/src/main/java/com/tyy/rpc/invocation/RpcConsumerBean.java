package com.tyy.rpc.invocation;

import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.util.ProxyFactoryUtils;
import org.springframework.beans.factory.FactoryBean;


/**
 * @author:tyy
 * @date:2021/7/11
 */
public class RpcConsumerBean implements FactoryBean {

    private ServiceMetaData serviceMetaData;

    @Override
    public Object getObject() throws Exception {
        ProxyFactory factory = ProxyFactoryUtils.getProxyFactory(ProxyFactoryUtils.PROXY_TYPE_JDK);
        return factory.getProxy(serviceMetaData);
    }

    public void setServiceMetaData(ServiceMetaData serviceMetaData) {
        this.serviceMetaData = serviceMetaData;
    }

    @Override
    public Class<?> getObjectType(){
        if(this.serviceMetaData == null) {
            return null;
        }
        return serviceMetaData.getClazz();
    }
}
