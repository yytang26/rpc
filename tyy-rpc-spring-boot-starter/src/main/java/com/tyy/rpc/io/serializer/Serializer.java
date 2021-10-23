package com.tyy.rpc.io.serializer;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface Serializer {

    String name();

    byte[] serialize(Object var) throws Exception;

    <T> T deserialize(byte[] var, Class<T> c) throws Exception;
}
