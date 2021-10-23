package com.tyy.rpc.domain;

import lombok.Data;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@Data
public class RpcResponse {


    private String requestId;

    private RpcStatusEnum status;

    private Object data;

    private Exception exception;

    public RpcResponse(RpcStatusEnum status) {
        this.status = status;
    }

}
