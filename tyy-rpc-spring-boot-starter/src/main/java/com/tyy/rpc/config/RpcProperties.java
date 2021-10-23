package com.tyy.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@ConfigurationProperties(
        prefix = "rpc"
)
@Data
public class RpcProperties {

    private String registerAddr = "127.0.0.1:2181";

    private Integer serverPort = 22100;

    private String serializer = "java";

    private String loadBalance = "random";

    private Integer weight = 1;
}
