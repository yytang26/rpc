package com.tyy.rpc.model;

import lombok.Data;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@Data
public class ServiceMetaData {
    public static final String DEFAULT_VERSION = "1.0.0";

    private String name;

    private Class<?> clazz;

    private String version;
}
