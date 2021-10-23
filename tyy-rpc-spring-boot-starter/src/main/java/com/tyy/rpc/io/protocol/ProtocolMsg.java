package com.tyy.rpc.io.protocol;

import lombok.Data;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Data
public class ProtocolMsg {

    private byte msgType;

    private byte[] content;
}
