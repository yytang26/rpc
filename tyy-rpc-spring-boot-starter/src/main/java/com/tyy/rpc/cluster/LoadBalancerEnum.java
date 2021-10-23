package com.tyy.rpc.cluster;

/**
 * @author:tyy
 * @date:2021/7/10
 */
public enum LoadBalancerEnum {

    RANDOM("random"),
    ROUND("round"),
    ;

    private String code;

    private LoadBalancerEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
