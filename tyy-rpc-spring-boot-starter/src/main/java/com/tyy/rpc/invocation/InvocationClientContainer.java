package com.tyy.rpc.invocation;

import com.tyy.rpc.io.client.DefaultInvocationClient;
import com.tyy.rpc.io.client.InvocationClient;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class InvocationClientContainer {

    public static InvocationClient getInvocationClient(String severNet){
        return DefaultInvocationClient.getInstance(severNet);
    }

}
