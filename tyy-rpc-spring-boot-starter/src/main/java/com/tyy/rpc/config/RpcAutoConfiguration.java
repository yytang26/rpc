package com.tyy.rpc.config;

import com.tyy.rpc.annotation.RpcProvider;
import com.tyy.rpc.context.BeanContext;
import com.tyy.rpc.invocation.InvocationServiceSelector;
import com.tyy.rpc.registry.BootStarter;
import com.tyy.rpc.registry.Registry;
import com.tyy.rpc.registry.nacos.NacosRegistry;
import com.tyy.rpc.registry.zookeeper.ZookeeperRegistry;
import com.tyy.rpc.util.SpiLoaderUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@Configuration
@EnableConfigurationProperties({RpcProperties.class})
public class RpcAutoConfiguration {

    @Bean("properties")
    public RpcProperties rpcProperties() {
        return new RpcProperties();
    }

    @Bean
    public BeanContext beanContext() {
        return new BeanContext();
    }

    @Bean
    public Registry registry(@Autowired RpcProperties rpcProperties) throws Exception {

        //zookeeper注册中心
        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry(
                rpcProperties.getServerPort(),
                rpcProperties.getSerializer(),
                rpcProperties.getWeight(),
                rpcProperties.getRegisterAddr());

        NacosRegistry nacosRegistry = new NacosRegistry(
                rpcProperties.getServerPort(),
                rpcProperties.getSerializer(),
                rpcProperties.getWeight(),
                rpcProperties.getRegisterAddr());

        InvocationServiceSelector.setRegistry(nacosRegistry);
        InvocationServiceSelector.setLoadBalancer(SpiLoaderUtils.getLoadBalancer(rpcProperties.getLoadBalance()));
        return nacosRegistry;
    }

    @Bean
    public BootStarter bootStarter(@Qualifier("properties") RpcProperties properties, @Autowired Registry registry) {
        return new BootStarter(registry, properties);
    }
}
