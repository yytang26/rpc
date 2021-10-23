package com.tyy.rpc.util;

import com.tyy.rpc.common.constants.RpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class DelimiterUtils {
    public static byte[] DELIMITE_BYTE;

    static {
        try {
            DELIMITE_BYTE = "$_*_#_@_$".getBytes(RpcConstant.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }

    public static ByteBuf getDelimiterByteBuf() {
        return Unpooled.copiedBuffer(DELIMITE_BYTE);
    }

    public static byte[] encodeDelimiterData(byte[] data) {
        byte[] dest = new byte[data.length+DELIMITE_BYTE.length];
        System.arraycopy(data,0,dest,0,data.length);
        System.arraycopy(DELIMITE_BYTE,0,dest,data.length,DELIMITE_BYTE.length);
        return dest;
    }
}
