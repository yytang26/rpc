package com.tyy.rpc.io.serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ProtobufSerializer implements Serializer {
    @Override
    public String name() {
        return SerializerEnum.PROTOBUF.getCode();
    }

    @Override
    public byte[] serialize(Object var) throws Exception {
        Schema schema = RuntimeSchema.getSchema(var.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        final byte[] result;
        try {
            result = ProtobufIOUtil.toByteArray(var,schema,buffer);
        } finally {
            buffer.clear();
        }
        return result;
    }

    @Override
    public <T> T deserialize(byte[] var, Class<T> c) throws Exception {
        Schema<T> schema = RuntimeSchema.getSchema(c);
        T t = schema.newMessage();
        ProtobufIOUtil.mergeFrom(var,t,schema);
        return t;
    }

}
