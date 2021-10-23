package com.tyy.rpc.domain;

import lombok.Data;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@Data
public class RpcRequest {

    private String requestId;

    private String serviceId;

    private String method;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
