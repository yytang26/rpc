package com.tyy.rpc.config;

import com.tyy.rpc.invocation.RpcConsumerFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author:tyy
 * @date:2021/7/11
 */

@Configuration
@ConditionalOnProperty(name = {"rpc.enabled"},matchIfMissing = true)
public class RpcConsumerAutoConfiguration {

    public RpcConsumerAutoConfiguration(){}

    @Bean
    public static BeanFactoryPostProcessor rpcConsumerPostProcessor(){
        return new RpcConsumerFactoryPostProcessor();
    }
}
