package com.tyy.rpc.io.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.*;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class JavaSerializer implements Serializer {

    @Override
    public String name() {
        return SerializerEnum.JAVA.getCode();
    }

    @Override
    public byte[] serialize(Object var) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(var);
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] var, Class<T> c) throws Exception {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(var));
        return (T)inputStream.readObject();
    }
}
