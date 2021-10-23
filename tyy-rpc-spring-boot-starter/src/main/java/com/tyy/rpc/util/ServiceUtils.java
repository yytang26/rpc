package com.tyy.rpc.util;

import com.tyy.rpc.model.ServiceMetaData;

import static com.tyy.rpc.common.constants.RpcConstant.*;

/**
 * @author:tyy
 * @date:2021/7/11
 */
public class ServiceUtils {

    /**
     * 获取服务id
     *
     * @param serviceMetaData
     * @return
     */
    public static String getServiceId(ServiceMetaData serviceMetaData) {
        return serviceMetaData.getName() + SERVICE_VERSION_DELIMITER + serviceMetaData.getVersion();
    }

    /**
     * 注册中心上的parent path
     *
     * @param serviceId
     * @return
     */
    public static String getRegisterServiceParentPath(String serviceId) {
        return ZK_SERVICE_PATH + SERVICE_PATH_DELIMITER + serviceId;
    }
}
