package com.tyy.rpc.io.client;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ClientServiceAddressHandlerCache {
    public static Map<String, ClientRequestHandler>

            HANDLER_CACHE = Maps.newConcurrentMap();

    /**
     * 设置地址handler
     *
     * @param address
     * @param handler
     */
    public static void put(String address, ClientRequestHandler handler) {
        HANDLER_CACHE.put(address, handler);
    }

    /**
     * 移除地址
     *
     * @param address
     */
    public static void remove(String address) {
        HANDLER_CACHE.remove(address);
    }

    /**
     * 获取地址handler
     *
     * @param address
     * @return
     */
    public static ClientRequestHandler get(String address) {
        return HANDLER_CACHE.get(address);
    }

    /**
     * 是否存在地址处理器
     *
     * @param address
     * @return
     */
    public static boolean exists(String address) {
        return HANDLER_CACHE.containsKey(address);
    }
}
