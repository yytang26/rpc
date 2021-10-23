package com.tyy.rpc.registry;

import com.alibaba.nacos.common.utils.StringUtils;
import com.tyy.rpc.annotation.RpcProvider;
import com.tyy.rpc.config.RpcProperties;
import com.tyy.rpc.io.server.RpcServerContainer;
import com.tyy.rpc.model.ServiceMetaData;
import com.tyy.rpc.registry.cache.ClientServiceDiscoveryCache;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Slf4j
public class BootStarter implements ApplicationListener<ContextRefreshedEvent> {

    private Registry registry;

    private RpcProperties properties;

    public BootStarter(Registry registry, RpcProperties properties) {
        this.registry = registry;
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(Objects.isNull(event.getApplicationContext().getParent())){
            ApplicationContext context = event.getApplicationContext();

            registerService(context);

            subscribeService(context);
        }
    }

    private void subscribeService(ApplicationContext context) {
        List<ServiceMetaData> serviceMetaDataList = ClientServiceDiscoveryCache.getAllServiceMetadata();
        for(ServiceMetaData serviceMetaData : serviceMetaDataList){
            registry.subscribe(serviceMetaData);
            registry.subscribeServiceChange(serviceMetaData);
        }
    }

    private void registerService(ApplicationContext context) {
        Map<String,Object> beanMap = context.getBeansWithAnnotation(RpcProvider.class);
        if(beanMap.size()>0){
            for(Object obj : beanMap.values()){
                Class<?> clazz = obj.getClass();
                try{
                    RpcProvider rpcProvider = clazz.getAnnotation(RpcProvider.class);
                    if(StringUtils.isEmpty(rpcProvider.name())){
                        throw new RuntimeException("");
                    }
                    ServiceMetaData serviceMetaData = new ServiceMetaData();
                    serviceMetaData.setName(rpcProvider.name());
                    serviceMetaData.setVersion(rpcProvider.version());
                    serviceMetaData.setClazz(clazz);
                    registry.registry(serviceMetaData);
                }catch (Exception e){
                    throw new RuntimeException("");
                }
            }
            new Thread(){
                @Override
                public void run() {
                    RpcServerContainer.initServer(properties).start();
                }
            }.start();
        }
    }
}
