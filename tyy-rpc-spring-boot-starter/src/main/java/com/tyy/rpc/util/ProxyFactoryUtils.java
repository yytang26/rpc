package com.tyy.rpc.util;

import com.tyy.rpc.invocation.JdkProxyFactory;
import com.tyy.rpc.invocation.ProxyFactory;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ProxyFactoryUtils {

    public static final String PROXY_TYPE_JDK = "jdk";

    private static JdkProxyFactory jdkProxyFactory = new JdkProxyFactory();

    public static ProxyFactory getProxyFactory(String type) {
        return jdkProxyFactory;
    }
}
