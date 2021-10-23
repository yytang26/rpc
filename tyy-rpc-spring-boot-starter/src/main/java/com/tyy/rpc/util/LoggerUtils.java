package com.tyy.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class LoggerUtils {

    public static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger("rpc");
    }
}
