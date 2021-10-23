package com.tyy.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Autowired
@Documented
public @interface RpcConsumer {

    String version() default "1.0.0";
}
