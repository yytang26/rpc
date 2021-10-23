package com.tyy.rpc.registry;

import lombok.Data;

import java.util.Objects;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@Data
public class ServiceURL {

    private String serviceId;

    private String name;

    private String version;

    private String serializer;

    private String address;

    private Integer weight;

    private String serverNet;

    @Override
    public String toString() {
        return "Service(" +
                "serviceId='" + serviceId + '\'' +
                ",serializer='" + serializer + '\'' +
                ",address='" + address + '\'' +
                '}';
    }
    @Override
    public int hashCode(){
        return Objects.hash(serviceId,serializer,address);
    }

    @Override
    public boolean equals(Object o){
        if(o==this){
            return true;
        }
        if(o==null || getClass()!=o.getClass()){
            return false;
        }
        ServiceURL serviceURL = (ServiceURL)o;
        return Objects.equals(serviceId, serviceURL.getServiceId()) &&
                Objects.equals(serializer, serviceURL.getSerializer()) &&
                Objects.equals(address, serviceURL.getAddress());
    }
}
