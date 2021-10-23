package com.tyy.rpc.exception;

/**
 * @author:tyy
 * @date:2021/7/10
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }
}
