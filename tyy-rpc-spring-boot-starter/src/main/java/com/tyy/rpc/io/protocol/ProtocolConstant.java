package com.tyy.rpc.io.protocol;

import lombok.Data;

/**
 * @author:tyy
 * @date:2021/7/11
 */
@Data
public class ProtocolConstant {

    public static final  byte MAGIC = 0x35;

    public static final byte DEFAULT_VERSION = 1;

    public static final byte REQ_TYPE = 0;

    public static final byte RES_TYPE = 1;
}
