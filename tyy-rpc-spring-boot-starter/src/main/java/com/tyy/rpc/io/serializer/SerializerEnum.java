package com.tyy.rpc.io.serializer;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public enum  SerializerEnum {

    JAVA("java"),

    PROTOBUF("protobuf"),
    HESSIAN("hessian");

    private String code;

    private SerializerEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
