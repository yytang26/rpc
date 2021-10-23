package com.tyy.rpc.io.client;

import com.tyy.rpc.io.serializer.Serializer;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public interface NetClient {

    ClientRequestHandler connect(String address, Serializer serializer);
}
