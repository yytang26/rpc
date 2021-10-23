package com.tyy.rpc.io.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class HessianSerializer implements Serializer {

    @Override
    public String name() {
        return SerializerEnum.HESSIAN.getCode();
    }

    @Override
    public byte[] serialize(Object var) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(bos);
        hessianOutput.writeObject(var);
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] var, Class<T> c) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(var);
        HessianInput hessianInput = new HessianInput(bis);

        return (T)hessianInput.readObject();
    }
}
